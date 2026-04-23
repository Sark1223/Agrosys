package com.agrosys.plot.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.agrosys.plot.dto.Response;
import com.agrosys.plot.dto.plantatio.PlantatioRegister;
import com.agrosys.plot.dto.plantatio.PlantatioStageRegister;
import com.agrosys.plot.repository.PlantatioRespository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlantatioService {
    private final PlantatioRespository plantatioRespository;

    public List<PlantatioRespository.PlantatioProjection> getAllPlantatioByPlotId(Integer plotId) {
        log.info("Obteniendo plantaciones por id de parcela: {}", plotId);
        return plantatioRespository.findByPlotId(plotId);
    }

    @Transactional
    public Response postPlantatio(PlantatioRegister request) {

        Integer existiong = plantatioRespository.existsByName(request.getName());
        if (existiong != null) {
            throw new IllegalArgumentException("El nombre de la plantacion ya existe");
        }

        Integer insert = plantatioRespository.insertPlantatio(request.getPlotId(), request.getName(),
                request.getStartAt(), request.getNotas());

        if (insert.equals(0)) {
            throw new RuntimeException("No se pudo registrar la plantacion");
        }

        return Response.builder()
                .message("Plantacion registrada exitosamente")
                .success(true)
                .build();
    }

    @Transactional
    public Response updatePlantatio(Integer plantatioId, PlantatioRegister request) {
        Integer existiong = plantatioRespository.existsByName(request.getName(), plantatioId);
        if (existiong != null) {
            throw new IllegalArgumentException("El nombre de la plantacion ya esta en uso...");
        }

        Integer insert;
        if (request.getEndAt().equals("false")) {
            insert = plantatioRespository.updatePlantatio(plantatioId, request.getPlotId(), request.getName(),
                    request.getStartAt(), request.getNotas());
        } else
            insert = plantatioRespository.updatePlantatio(plantatioId, request.getPlotId(), request.getName(),
                    request.getStartAt(), request.getEndAt(), request.getNotas());

        if (insert.equals(0)) {
            throw new RuntimeException("No se pudo actualizar la plantacion");
        }

        return Response.builder()
                .message("Plantacion actualizada exitosamente")
                .success(true)
                .build();
    }

    @Transactional
    public Response deletePlantatio(Integer plantatioId) {
        Integer delete = plantatioRespository.deletePlantatio(plantatioId);

        if (delete.equals(0)) {
            throw new RuntimeException("No se pudo eliminar la plantacion");
        }

        return Response.builder()
                .message("Plantacion eliminada exitosamente")
                .success(true)
                .build();
    }

    // ====== Plantatio Stage ======
    @Transactional
    public Response insertPlantatioStage(PlantatioStageRegister request) throws JsonProcessingException {

        PlantatioRespository.DatesPlantationProjection plantation = plantatioRespository
                .getDatesPlantatioById(request.getPlantatioId());
        if (plantation == null) {
            throw new IllegalArgumentException("La plantacion no existe");
        }

        // Parsear el JSON directamente a una lista de mapas
        ObjectMapper mapper = new ObjectMapper();
        log.info("Stages JSON: {}", plantation.getStages());
        List<Map<String, Object>> stages = mapper.readValue(
                plantation.getStages(),
                new TypeReference<List<Map<String, Object>>>() {
                });

        log.info("Lo converti: {}", plantation.getStages());

        for (Map<String, Object> stage : stages) {
            Integer stageId = (Integer) stage.get("stageId");
            String startAtStage = (String) stage.get("startAtStage");
            String endAtStage = (String) stage.get("endAtStage");
            System.out.println(stageId + " - " + startAtStage + " - " + endAtStage);
        }

        LocalDate startAtRequest = LocalDate.parse(request.getStartAt());
        LocalDate endAtRequest = null;
        if (!request.getEndAt().equals("false")) {
            endAtRequest = LocalDate.parse(request.getEndAt());

            if (endAtRequest.isBefore(startAtRequest)) {
                throw new IllegalArgumentException(
                        "La fecha de fin de la etapa debe ser posterior a la fecha de inicio");
            }
        }

        Integer insert;
        switch (request.getStageId()) {
            case 1 -> {

                Object stageIdObj = stages.get(0).get("stageId");
                if (stageIdObj != null) {
                    throw new IllegalArgumentException(
                            "No se puede registrar la etapa de PREPARACION porque ya existen etapas registradas para esta plantacion");
                }
                // Actualizar fecha de inicio de la plantacion
                Integer updatePlantatioStartDate = plantatioRespository.updatePlantatioStartDate(
                        request.getPlantatioId(), request.getStartAt());
                if (updatePlantatioStartDate.equals(0)) {
                    throw new RuntimeException("No se pudo actualizar la fecha de inicio de la plantacion");
                }

            }
            case 2 -> {
                Object stageIdObj = stages.get(0).get("stageId");
                if (stages.size() >= 2 || stageIdObj == null || !stages.get(0).get("stageId").equals(1)) {
                    throw new IllegalArgumentException(
                            "No se puede registrar la etapa de SIEMBRA porque no existe la etapa de PREPARACIÓN o se registro la etapa");
                }

                // verificar que la fecha de inicio de la etapa de siembra sea posterior a la
                // fecha de inicio de la etapa de preparación
                String startAtPreparation = (String) stages.get(0).get("startAtStage");
                LocalDate startAtPreparationDate = LocalDate.parse(startAtPreparation);
                if (startAtRequest.isBefore(startAtPreparationDate)) {
                    throw new IllegalArgumentException(
                            "La fecha de inicio de la etapa de SIEMBRA debe ser posterior a la fecha de inicio de la etapa de PREPARACIÓN");
                }
            }
            case 3 -> {
                if (stages.size() != 2 || !stages.get(1).get("stageId").equals(2)) {
                    throw new IllegalArgumentException(
                            "No se puede registrar la etapa de COCECHA porque no existen las etapas de PREPARACIÓN y SIEMBRA o se registro la etapa");
                }
                // verificar que la fecha de inicio de la etapa de cosecha sea posterior a la
                // fecha de inicio de la etapa de siembra
                String startAtSembra = (String) stages.get(1).get("startAtStage");
                LocalDate startAtSembraDate = LocalDate.parse(startAtSembra);
                if (startAtRequest.isBefore(startAtSembraDate)) {
                    throw new IllegalArgumentException(
                            "La fecha de inicio de la etapa de COSECHA debe ser posterior a la fecha de inicio de la etapa de SIEMBRA");
                }

                if (endAtRequest != null) {
                    // Actualizar fecha de fin de la plantacion
                    Integer updatePlantatioEndDate = plantatioRespository.updatePlantatioEndDate(
                            request.getPlantatioId(),
                            request.getStageId(), request.getEndAt());
                    if (updatePlantatioEndDate.equals(0)) {
                        throw new RuntimeException("No se pudo actualizar la fecha de fin de la plantacion");
                    }
                }
            }
            default -> {

                throw new IllegalArgumentException(
                        "La etapa de la plantacion no es valida");
            }
        }

        if (endAtRequest == null)
            insert = plantatioRespository.insertPlantatioStage(request.getPlantatioId(), request.getStageId(),
                    request.getStartAt(), request.getNotas());
        else
            insert = plantatioRespository.insertPlantatioStage(request.getPlantatioId(), request.getStageId(),
                    request.getStartAt(), request.getEndAt(), request.getNotas());

        if (insert.equals(0)) {
            throw new RuntimeException("No se pudo registrar la etapa de la plantacion");
        }

        return Response.builder()
                .message("Etapa de plantacion registrada exitosamente")
                .success(true)
                .build();
    }

    public List<PlantatioRespository.PlantatioStageProjection> getStagesByPlantationId(Integer plotId) {
        return plantatioRespository.getStagesByPlantatio(plotId);
    }

    // @Transactional
    // public Response updatePlantatioStage(PlantatioStageRegister request) {
    // String endAt =
    // plantatioRespository.getEndAtPlantatioStage(request.getPlantatioId(),
    // request.getStageId());
    // LocalDate endAtDate = LocalDate.parse(endAt);
    // LocalDate startAtRequest = LocalDate.parse(request.getStartAt());
    // if (endAtDate.isBefore(startAtRequest)) {
    // throw new IllegalArgumentException("La fecha de inicio de la etapa debe ser
    // posterior a la fecha de fin de la etapa anterior");
    // }

    // Integer insert =
    // plantatioRespository.updatePlantatioStage(request.getPlantatioId(),
    // request.getStageId(), request.getStartAt(), request.getNotas());

    // if (insert.equals(0)) {
    // throw new RuntimeException("No se pudo actualizar la etapa de la
    // plantacion");
    // }

    // return Response.builder()
    // .message("Etapa de plantacion actualizada exitosamente")
    // .success(true)
    // .build();
    // }
}
