package com.agrosys.worker.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agrosys.worker.dto.Response;
import com.agrosys.worker.dto.WorkerRequest;
import com.agrosys.worker.dto.WorkerResponse;
import com.agrosys.worker.dto.WorkerUpdateRequest;
import com.agrosys.worker.repository.WorkerRepository;
import com.agrosys.worker.service.WorkerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/workers")
@RequiredArgsConstructor
@Slf4j
public class AgrosysWorkerController {

    private final WorkerService workerService;

    @GetMapping("/get-all")
    @PreAuthorize("hasAuthority('MODULE_TRABAJADORES')")
    public ResponseEntity<Response> getAllWorkers() {
        List<WorkerRepository.WorkerProjection> workers = workerService.getAllWorkers();
        Response successResponse = Response.builder()
                .success(true)
                .message("Workers obtenidos exitosamente")
                .data(workers)
                .build();
        return ResponseEntity.ok(successResponse);
    }

    @PostMapping("/register")
    @PreAuthorize("hasAuthority('MODULE_TRABAJADORES')")
    public ResponseEntity<Response> createWorker(@Valid @RequestBody WorkerRequest request) {
        log.info("[REQUEST] - request: {}", request);
        WorkerResponse response = workerService.crearWorker(request);
        Response successResponse = Response.builder()
                .success(true)
                .message("Worker registrado exitosamente")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(successResponse);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('MODULE_TRABAJADORES')")
    public ResponseEntity<Response> deleteWorker(@RequestBody Map<String, Integer> body) {
        Integer workerId = body.get("id");  // ← espera "id"
        if (workerId == null) {
            return ResponseEntity.badRequest()
                    .body(Response.builder()
                            .success(false)
                            .message("ID del worker no proporcionado")
                            .build());
        }
        try {
            workerService.deleteWorker(workerId);
            return ResponseEntity.ok(Response.builder()
                    .success(true)
                    .message("Worker eliminado exitosamente")
                    .build());
        } catch (Exception e) {
            log.error("Error al eliminar worker: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.builder()
                            .success(false)
                            .message("Error al eliminar worker: " + e.getMessage())
                            .build());
        }
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('MODULE_TRABAJADORES')")
    public ResponseEntity<Response> updateWorker(@Valid @RequestBody WorkerUpdateRequest request) {
        log.info("[REQUEST UPDATE] - workerId: {}, request: {}", request.getWorkerId(), request);
        try {
            WorkerResponse response = workerService.actualizarWorker(request.getWorkerId(), request);
            return ResponseEntity.ok(Response.builder()
                    .success(true)
                    .message("Worker actualizado exitosamente")
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("Error al actualizar worker: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        }
    }
}
