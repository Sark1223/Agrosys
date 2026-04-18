package com.agrosys.worker.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.agrosys.worker.dto.WorkerRequest;
import com.agrosys.worker.dto.WorkerResponse;
import com.agrosys.worker.repository.WorkerRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkerService {

    private final WorkerRepository workerRepository;

    public List<WorkerRepository.WorkerProjection> getAllWorkers() {
        return workerRepository.findAllWorkers();
    }

    @Transactional
    public WorkerResponse crearWorker(WorkerRequest request) {
        log.info("[REQUEST] - request: {}", request);

        if (workerRepository.existsByName(request.getName()) != null) {
            log.warn("[FIELD VIOLATION] - El nombre del worker ya está en uso: {}", request.getName());
            throw new RuntimeException("El nombre del worker ya está en uso");
        }

        Integer worker = workerRepository.insertWorker(request.getName(), request.getNotas());

        if (worker == null || worker == 0) {
            log.error("[FAILED] - No se pudo registrar el worker");
            throw new RuntimeException("No se pudo registrar el worker");
        }

        WorkerRepository.WorkerProjection saved = workerRepository.findByName(request.getName());
        if (saved == null) {
            log.error("[FAILED] - Error al recuperar el worker registrado: {}", request.getName());
            throw new RuntimeException("No se pudo recuperar el worker registrado");
        }

        log.info("[SUCCESS] - Worker registrado exitosamente: {} con ID: {}",
                saved.getName(), saved.getWorkerId());

        return WorkerResponse.builder()
                .workerId(saved.getWorkerId())
                .name(saved.getName())
                .notas(saved.getNotas())
                .message("Worker registrado exitosamente")
                .success(true)
                .build();
    }

    @Transactional
    public void deleteWorker(Integer workerId) {
        if (workerRepository.countById(workerId) == 0) {
            throw new RuntimeException("Worker no encontrado con ID: " + workerId);
        }
        workerRepository.deleteWorkerById(workerId);
    }
}
