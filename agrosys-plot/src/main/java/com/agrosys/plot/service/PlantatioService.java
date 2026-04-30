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
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
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
                request.getNotas());

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

        Integer insert = plantatioRespository.updatePlantatio(plantatioId, request.getPlotId(), request.getName(),
                request.getNotas());

        if (insert == null) {
            throw new RuntimeException("No se pudo crear la plantacion");
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
                            "La etapa de PREPARACIÓN ya fue registrada para esta plantación");
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
                            "No se puede registrar la etapa de SIEMBRA porque no existe la etapa de PREPARACIÓN ó ya existe la etapa de SIEMBRA registrada");
                }

                // verificar que la fecha de inicio de la etapa de siembra sea posterior a la
                // fecha de inicio de la etapa de preparación
                String startAtPreparation = (String) stages.get(0).get("startAtStage");
                LocalDate startAtPreparationDate = LocalDate.parse(startAtPreparation);
                if (startAtRequest.isBefore(startAtPreparationDate)) {
                    throw new IllegalArgumentException(
                            "La fecha de inicio de la etapa de SIEMBRA debe ser posterior a la fecha de inicio de la etapa de PREPARACIÓN");
                }

                // Actualizar fecha de fin de la etapa de preparación
                Integer updatePreparationEndDate = plantatioRespository.updatePlantatioStageEndDate(
                        request.getPlantatioId(), 1, request.getStartAt());

                if (updatePreparationEndDate.equals(0))
                    throw new RuntimeException("No se pudo actualizar la fecha de fin de la etapa de preparación");
            }
            case 3 -> {
                if (stages.size() != 2 || !stages.get(1).get("stageId").equals(2)) {
                    throw new IllegalArgumentException(
                            "No se puede registrar la etapa de COCECHA porque no existen la etapa de SIEMBRA ó ya existe la etapa de COSECHA registrada");
                }
                // verificar que la fecha de inicio de la etapa de cosecha sea posterior a la
                // fecha de inicio de la etapa de siembra
                String startAtSembra = (String) stages.get(1).get("startAtStage");
                LocalDate startAtSiembraDate = LocalDate.parse(startAtSembra);
                if (startAtRequest.isBefore(startAtSiembraDate)) {
                    throw new IllegalArgumentException(
                            "La fecha de inicio de la etapa de COSECHA debe ser posterior a " + startAtSiembraDate
                                    + " fecha de SIEMBRA");
                }

                // Actualizar fecha de fin de la etapa de siembra
                Integer updateSembraEndDate = plantatioRespository.updatePlantatioStageEndDate(
                        request.getPlantatioId(), 2, request.getStartAt());
                if (updateSembraEndDate.equals(0)) {
                    throw new RuntimeException("No se pudo actualizar la fecha de fin de la etapa de siembra");
                }
            }
            case 4 -> {
                if (stages.size() != 3 || !stages.get(2).get("stageId").equals(3)) {
                    throw new IllegalArgumentException(
                            "No se puede registrar la etapa de FINALIZADO porque no existe la etapa de COSECHA ó ya existe la etapa de FINALIZADO");
                }
                // verificar que la fecha de inicio de la etapa de finalizado sea posterior a la
                // fecha de inicio de la etapa de cosecha
                String startAtCosecha = (String) stages.get(2).get("startAtStage");
                LocalDate startAtCosechaDate = LocalDate.parse(startAtCosecha);
                if (startAtRequest.isBefore(startAtCosechaDate)) {
                    throw new IllegalArgumentException(
                            "La fecha de inicio de la etapa de FINALIZADO debe ser posterior a " + startAtCosechaDate
                                    + " fecha de COSECHA");
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

                // Actualizar fecha de fin de la etapa de cosecha
                Integer updateCosechaEndDate = plantatioRespository.updatePlantatioStageEndDate(
                        request.getPlantatioId(), 3, request.getStartAt());
                if (updateCosechaEndDate.equals(0)) {
                    throw new RuntimeException("No se pudo actualizar la fecha de fin de la etapa de cosecha");
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

    public Response updatePlantatioStage(PlantatioStageRegister request)
            throws JsonMappingException, JsonProcessingException {

        if (request.getEndAt() == null || request.getStartAt() == null) {
            throw new IllegalArgumentException("La fecha de inicio y fin son requeridas");
        }

        PlantatioRespository.DatesPlantationProjection plantation = plantatioRespository
                .getDatesPlantatioById(request.getPlantatioId());
        if (plantation == null) {
            throw new IllegalArgumentException("La plantacion no existe");
        }

        ObjectMapper mapper = new ObjectMapper();
        log.info("Stages JSON: {}", plantation.getStages());
        List<Map<String, Object>> stages = mapper.readValue(
                plantation.getStages(),
                new TypeReference<List<Map<String, Object>>>() {
                });

        LocalDate startAtRequest = LocalDate.parse(request.getStartAt());
        LocalDate endAtRequest = LocalDate.parse(request.getEndAt());
        if (endAtRequest.isBefore(startAtRequest)) {
            throw new IllegalArgumentException("La fecha de fin debe ser posterior a la fecha de inicio");
        }

        Integer noStages = stages.size();
        Object stageIdObj = stages.get(0).get("stageId");
        if (noStages == 0 || stageIdObj == null) {
            throw new IllegalArgumentException("No se han registrado etapas para esta plantacion");
        }
        switch (request.getStageId()) {
            case 1 -> {
                if (noStages > 1) {
                    throw new IllegalArgumentException(
                            "No se puede actualizar la etapa de PREPARACIÓN porque ya existe la etapa de SIEMBRA registrada");
                }

                LocalDate startPlantatio = LocalDate.parse(plantation.getStart_at());
                if (!startAtRequest.isEqual(startPlantatio)) {
                    Integer updatePlantatioStartDate = plantatioRespository.updatePlantatioStartDate(
                            request.getPlantatioId(), request.getStartAt());

                    if (updatePlantatioStartDate.equals(0)) {
                        throw new RuntimeException("No se pudo actualizar la fecha de inicio de la plantacion");
                    }
                }
            }
            case 2 -> {
                if (noStages < 2 || !stages.get(1).get("stageId").equals(2)) {
                    throw new IllegalArgumentException(
                            "La etapa de SIEMBRA no existe para esta plantación");
                }

                if (noStages > 2) {
                    throw new IllegalArgumentException(
                            "No se puede actualizar la etapa de SIEMBRA porque ya existe la etapa de COSECHA registrada");
                }

                String startAtPreparation = (String) stages.get(0).get("startAtStage");
                LocalDate startAtPreparationDate = LocalDate.parse(startAtPreparation);
                if (startAtRequest.isBefore(startAtPreparationDate)) {
                    throw new IllegalArgumentException(
                            "La fecha de inicio de la etapa de SIEMBRA debe ser posterior a la fecha de inicio de la etapa de PREPARACIÓN");
                }

                String endAtPreparation = (String) stages.get(0).get("endAtStage");
                LocalDate endAtPreparationDate = LocalDate.parse(endAtPreparation);
                if (!startAtRequest.isEqual(endAtPreparationDate)) {
                    // Actualizar fecha de fin de la etapa de preparación
                    Integer updatePreparationEndDate = plantatioRespository.updatePlantatioStageEndDate(
                            request.getPlantatioId(), 1, request.getStartAt());
                    if (updatePreparationEndDate.equals(0)) {
                        throw new RuntimeException("No se pudo actualizar la fecha de fin de la etapa de preparación");
                    }
                }
            }
            case 3 -> {
                if (noStages < 3 || !stages.get(2).get("stageId").equals(3)) {
                    throw new IllegalArgumentException(
                            "La etapa de COSECHA no existe para esta plantación");
                }

                String startAtSembra = (String) stages.get(1).get("startAtStage");
                LocalDate startAtSembraDate = LocalDate.parse(startAtSembra);
                if (startAtRequest.isBefore(startAtSembraDate)) {
                    throw new IllegalArgumentException(
                            "La fecha de inicio de la etapa de COSECHA debe ser posterior a la fecha de inicio de la etapa de SIEMBRA");
                }

                String endAtSembra = (String) stages.get(1).get("endAtStage");
                LocalDate endAtSembraDate = LocalDate.parse(endAtSembra);
                if (!startAtRequest.isEqual(endAtSembraDate)) {
                    // Actualizar fecha de fin de la etapa de siembra
                    Integer updateSembraEndDate = plantatioRespository.updatePlantatioStageEndDate(
                            request.getPlantatioId(), 2, request.getStartAt());
                    if (updateSembraEndDate.equals(0)) {
                        throw new RuntimeException("No se pudo actualizar la fecha de fin de la etapa de siembra");
                    }
                }
            }
            case 4 -> {
                if (noStages < 4 || !stages.get(3).get("stageId").equals(4)) {
                    throw new IllegalArgumentException(
                            "La etapa de FINALIZADO no existe para esta plantación");
                }

                String startAtCosecha = (String) stages.get(2).get("startAtStage");
                LocalDate startAtCosechaDate = LocalDate.parse(startAtCosecha);
                if (startAtRequest.isBefore(startAtCosechaDate)) {
                    throw new IllegalArgumentException(
                            "La fecha de inicio de la etapa de FINALIZADO debe ser posterior a la fecha de inicio de la etapa de COSECHA");
                }

                String endAtPlantation = plantation.getEnd_at();
                if (endAtPlantation != null) {
                    LocalDate endAtPlantationDate = LocalDate.parse(endAtPlantation);
                    if (!endAtRequest.isEqual(endAtPlantationDate)) {
                        // Actualizar fecha de fin de la plantacion
                        Integer updatePlantatioEndDate = plantatioRespository.updatePlantatioEndDate(
                                request.getPlantatioId(), request.getStageId(), request.getEndAt());
                        if (updatePlantatioEndDate.equals(0)) {
                            throw new RuntimeException("No se pudo actualizar la fecha de fin de la plantacion");
                        }
                    }
                } else {
                    // Actualizar fecha de fin de la plantacion
                    Integer updatePlantatioEndDate = plantatioRespository.updatePlantatioEndDate(
                            request.getPlantatioId(), request.getStageId(), request.getEndAt());
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

        Integer update = plantatioRespository.updatePlantatioStage(request.getPlantatioId(), request.getStageId(),
                request.getStartAt(), request.getEndAt(),
                request.getNotas());

        if (update.equals(0)) {
            throw new RuntimeException("No se pudo actualizar la etapa de la plantacion");
        }

        return Response.builder()
                .message("Etapa de plantacion actualizada exitosamente")
                .success(true)
                .build();
    }
}
