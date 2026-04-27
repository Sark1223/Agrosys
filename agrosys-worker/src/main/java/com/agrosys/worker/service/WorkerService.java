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
        log.info("[DELETE] - Intentando eliminar worker con ID: {}", workerId);
        WorkerRepository.WorkerProjection worker = workerRepository.findByIdWorker(workerId);
        if (worker == null) {
            log.error("[DELETE] - Worker no encontrado con ID: {}", workerId);
            throw new RuntimeException("Worker no encontrado con ID: " + workerId);
        }

        log.info("[DELETE] - Worker encontrado, photoPublicId: {}", worker.getPhotoPublicId());
        
        if (worker.getPhotoPublicId() != null && !worker.getPhotoPublicId().isEmpty()) {
            try {
                log.info("[DELETE] - Eliminando imagen de Cloudinary: {}", worker.getPhotoPublicId());
                imageService.deleteImage(worker.getPhotoPublicId());
                log.info("[DELETE] - Imagen eliminada exitosamente de Cloudinary");
            } catch (Exception e) {
                log.error("[DELETE ERROR] - No se pudo eliminar la imagen de Cloudinary: {}", e.getMessage());
            }
        }

        workerRepository.deleteWorkerById(workerId);
        log.info("[DELETE] - Worker eliminado de base de datos");
    }

    @Transactional
    public Response actualizarWorker(Integer workerId, WorkerRequest request) {
        log.info("[REQUEST UPDATE] - workerId: {}, request: {}", workerId, request);

        if (workerRepository.countById(workerId) == 0) {
            throw new RuntimeException("Worker no encontrado con ID: " + workerId);
        }

        String notas = request.getNotas() != null ? request.getNotas() : null;
        String photo = null;
        String photoPublicId = null;

        WorkerRepository.WorkerProjection existing = workerRepository.findByIdWorker(workerId);
        boolean removingPhoto = (request.getPhoto() == null || request.getPhoto().isEmpty()) 
            && existing.getPhoto() != null;

        if (request.getPhoto() != null && !request.getPhoto().isEmpty()) {
            photoPublicId = existing.getPhotoPublicId() != null ? existing.getPhotoPublicId() : "worker_" + workerId;
            photo = imageService.uploadImage(request.getPhoto(), photoPublicId);
        } else if (removingPhoto) {
            if (existing.getPhotoPublicId() != null && !existing.getPhotoPublicId().isEmpty()) {
                try {
                    log.info("[UPDATE] - Eliminando imagen anterior de Cloudinary: {}", existing.getPhotoPublicId());
                    imageService.deleteImage(existing.getPhotoPublicId());
                } catch (Exception e) {
                    log.warn("[UPDATE WARN] - No se pudo eliminar la imagen anterior: {}", e.getMessage());
                }
            }
            photo = null;
            photoPublicId = null;
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