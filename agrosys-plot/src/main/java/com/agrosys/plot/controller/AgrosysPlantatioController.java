package com.agrosys.plot.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.agrosys.plot.dto.Response;
import com.agrosys.plot.dto.plantatio.PlantatioRegister;
import com.agrosys.plot.dto.plantatio.PlantatioStageRegister;
import com.agrosys.plot.repository.PlantatioRespository;
import com.agrosys.plot.service.PlantatioService;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/plantation")
@RequiredArgsConstructor
@Slf4j
public class AgrosysPlantatioController {
    private final PlantatioService plantatioService;

    @GetMapping("/get-all")
    @PreAuthorize("hasAuthority('MODULE_PARCELAS')")
    public ResponseEntity<Object> getAllPlantations(
            @RequestParam String initDate,
            @RequestParam String endDate) {
        Response response = plantatioService.getAllPlantations(initDate, endDate);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-all-by-plot-id/{plotId}")
    @PreAuthorize("hasAuthority('MODULE_PARCELAS')")
    public ResponseEntity<Object> getAllPlantatioByPlotId(@PathVariable Integer plotId) {
        List<PlantatioRespository.PlantatiosByPlotProjection> plantatio = plantatioService.getAllPlantatioByPlotId(plotId);
        Response successResponse = Response.builder()
                .success(true)
                .message("Plantaciones obtenidas exitosamente")
                .data(plantatio)
                .build();
        return ResponseEntity.ok(successResponse);
    }

    @PostMapping("/post")
    @PreAuthorize("hasAuthority('MODULE_PARCELAS')")
    public ResponseEntity<Object> postPlantatio(@Valid @RequestBody PlantatioRegister request) {
        log.info("[REQUEST] - request: {}", request);
        Response response = plantatioService.postPlantatio(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('MODULE_PARCELAS')")
    public ResponseEntity<Object> putMethodName(@PathVariable Integer id,
            @Valid @RequestBody PlantatioRegister request) {
        log.info("[REQUEST] - request: {}", request);
        Response response = plantatioService.updatePlantatio(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('MODULE_PARCELAS')")
    public ResponseEntity<Object> deletePlantatio(@PathVariable Integer id) {
        log.info("[REQUEST] - id: {}", id);
        Response response = plantatioService.deletePlantatio(id);
        return ResponseEntity.ok(response);
    }

    // ================================= Stages =================================
    @PostMapping("/insert-plantation-stage")
    @PreAuthorize("hasAuthority('MODULE_PARCELAS')")
    public ResponseEntity<Object> postMethodName(@Valid @RequestBody PlantatioStageRegister request)
            throws JsonProcessingException {
        log.info("[REQUEST] - request:", request);
        Response response = plantatioService.insertPlantatioStage(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-plantation-stage")
    @PreAuthorize("hasAuthority('MODULE_PARCELAS')")
    public ResponseEntity<Object> putMethodName(@Valid @RequestBody PlantatioStageRegister request)
            throws JsonProcessingException {
        log.info("[REQUEST] - request:", request);
        Response response = plantatioService.updatePlantatioStage(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stages/by-plantation-id/{plotId}")
    @PreAuthorize("hasAuthority('MODULE_PARCELAS')")
    public ResponseEntity<Object> getStagesByPlantationId(@PathVariable Integer plotId) {
        List<PlantatioRespository.PlantatioStageProjection> plantatioStages = plantatioService
                .getStagesByPlantationId(plotId);
        Response successResponse = Response.builder()
                .success(true)
                .message("Estados de plantacion obtenidos exitosamente")
                .data(plantatioStages)
                .build();
        return ResponseEntity.ok(successResponse);
    }

    @DeleteMapping("delete/plantations/{plantationId}/stage/{id}")
    @PreAuthorize("hasAuthority('MODULE_PARCELAS')")
    public ResponseEntity<Object> deleteStageOfPlantatio(@PathVariable Integer plantationId, @PathVariable Integer id) {
        log.info("[REQUEST] - plantationId: {}, id: {}", plantationId, id);
        Response response = plantatioService.deleteStageOfPlantatio(plantationId, id);
        return ResponseEntity.ok(response);
    }
}
