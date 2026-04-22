package com.agrosys.plot.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.agrosys.plot.dto.Response;
import com.agrosys.plot.dto.plantatio.PlantatioRegister;
import com.agrosys.plot.dto.plantatio.PlantatioStageRegister;
import com.agrosys.plot.repository.PlantatioRespository;

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

    @Transactional
    public Response insertPlantatioStage(PlantatioStageRegister request) {

        if (request.getStageId().equals(1)) {
            String startAt = plantatioRespository.getStartAtPlantatio(request.getPlantatioId());
            LocalDate startAtDate = LocalDate.parse(startAt);
            LocalDate startAtRequest = LocalDate.parse(request.getStartAt());
            if (!startAtDate.isEqual(startAtRequest)) {
                throw new IllegalArgumentException(
                        "La fecha de inicio de la etapa debe ser igual a la fecha de inicio de la plantacion");
            }
        } else {
            String endAt = plantatioRespository.getEndAtPlantatioStage(request.getPlantatioId(), request.getStageId());
            LocalDate endAtDate = LocalDate.parse(endAt);
            LocalDate startAtRequest = LocalDate.parse(request.getStartAt());
            if (endAtDate.isBefore(startAtRequest)) {
                throw new IllegalArgumentException(
                        "La fecha de inicio de la etapa debe ser posterior a la fecha de fin de la etapa anterior");
            }
        }

        Integer insert = plantatioRespository.insertPlantatioStage(request.getPlantatioId(), request.getStageId(),
                request.getStartAt(), request.getNotas());

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
