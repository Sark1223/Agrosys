package com.agrosys.worker.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.agrosys.worker.dto.Response;
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
    private final ImageService imageService;

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

        String notas = request.getNotas() != null ? request.getNotas() : null;

        Integer worker = workerRepository.insertWorker(
                request.getName(),
                notas,
                request.getSalary(),
                null,
                null);

        if (worker == null || worker == 0) {
            log.error("[FAILED] - No se pudo registrar el worker");
            throw new RuntimeException("No se pudo registrar el worker");
        }

        WorkerRepository.WorkerProjection saved = workerRepository.findByName(request.getName());
        if (saved == null) {
            log.error("[FAILED] - Error al recuperar el worker registrado: {}", request.getName());
            throw new RuntimeException("No se pudo recuperar el worker registrado");
        }

        String photo = null;
        String photoPublicId = null;

        if (request.getPhoto() != null && !request.getPhoto().isEmpty()) {
            photoPublicId = "worker_" + saved.getWorkerId();
            photo = imageService.uploadImage(request.getPhoto(), photoPublicId);
            workerRepository.updateWorker(
                    saved.getWorkerId(),
                    saved.getName(),
                    saved.getNotas(),
                    saved.getSalary(),
                    photo,
                    photoPublicId);
            saved = workerRepository.findByName(request.getName());
        }

        log.info("[SUCCESS] - Worker registrado exitosamente: {} con ID: {}",
                saved.getName(), saved.getWorkerId());

        return WorkerResponse.builder()
                .workerId(saved.getWorkerId())
                .name(saved.getName())
                .notas(saved.getNotas())
                .salary(saved.getSalary())
                .photo(saved.getPhoto())
                .photoPublicId(saved.getPhotoPublicId())
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

    @Transactional
    public Response actualizarWorker(Integer workerId, WorkerRequest request) {
        log.info("[REQUEST UPDATE] - workerId: {}, request: {}", workerId, request);

        if (workerRepository.countById(workerId) == 0) {
            throw new RuntimeException("Worker no encontrado con ID: " + workerId);
        }

        Integer existingName = workerRepository.existsByNameExcludingId(request.getName(), workerId);
        if (existingName != null) {
            log.warn("[FIELD VIOLATION] - El nombre del worker ya está en uso: {}", request.getName());
            throw new RuntimeException("El nombre del worker ya está en uso");
        }

        String notas = request.getNotas() != null ? request.getNotas() : null;
        String photo = null;
        String photoPublicId = null;

        if (request.getPhoto() != null && !request.getPhoto().isEmpty()) {
            WorkerRepository.WorkerProjection existing = workerRepository.findByIdWorker(workerId);
            photoPublicId = existing.getPhotoPublicId() != null ? existing.getPhotoPublicId() : "worker_" + workerId;
            photo = imageService.uploadImage(request.getPhoto(), photoPublicId);
        }

        Integer updated = workerRepository.updateWorker(
                workerId,
                request.getName(),
                notas,
                request.getSalary(),
                photo,
                photoPublicId);

        if (updated == null || updated == 0) {
            log.error("[FAILED] - No se pudo actualizar el worker");
            throw new RuntimeException("No se pudo actualizar el worker");
        }

        WorkerRepository.WorkerProjection saved = workerRepository.findByName(request.getName());
        if (saved == null) {
            log.error("[FAILED] - Error al recuperar el worker actualizado: {}", request.getName());
            throw new RuntimeException("No se pudo recuperar el worker actualizado");
        }

        log.info("[SUCCESS] - Worker actualizado exitosamente: {} con ID: {}",
                saved.getName(), saved.getWorkerId());

        return Response.builder()
                .success(true)
                .message("Worker actualizado exitosamente")
                .data(null)
                .build();
    }
}