package com.agrosys.worker.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agrosys.worker.dto.DeleteRequest;
import com.agrosys.worker.dto.Response;
import com.agrosys.worker.dto.WorkerRequest;
import com.agrosys.worker.dto.WorkerResponse;
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
    public ResponseEntity<Response> deleteWorker(@RequestBody DeleteRequest request) {
        Integer workerId = request.getWorkerId();
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
            throw new RuntimeException("Error al eliminar worker: " + e.getMessage());
        
        }
    }

    @PutMapping("/update/{workerId}")
    @PreAuthorize("hasAuthority('MODULE_TRABAJADORES')")
    public ResponseEntity<Response> updateWorker(@PathVariable Integer workerId, 
        @Valid @RequestBody WorkerRequest request) {
        log.info("[REQUEST UPDATE] - workerId: {}, request: {}", workerId, request);
        try {
            Response response = workerService.actualizarWorker(workerId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al actualizar worker: {}", e.getMessage());
            throw new RuntimeException("Error al actualizar worker: " + e.getMessage());
        }
    }
}
