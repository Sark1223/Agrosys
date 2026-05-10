package com.agrosys.worker.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

        BigDecimal hourlyPay = request.getSalary().divide(BigDecimal.valueOf(56),2,RoundingMode.HALF_UP);
        String notas = request.getNotas() != null ? request.getNotas() : null;

        Integer worker = workerRepository.insertWorker(
                request.getName(),
                notas,
                request.getSalary(),
                null,
                null,
                hourlyPay
            );

        if (worker == null || worker == 0) {
            log.error("[FAILED] - No se pudo registrar el trabajador");
            throw new RuntimeException("No se pudo registrar el trabajador");
        }

        WorkerRepository.WorkerProjection saved = workerRepository.findByName(request.getName());
        if (saved == null) {
            log.error("[FAILED] - Error al recuperar el trabajador registrado: {}", request.getName());
            throw new RuntimeException("No se pudo recuperar el trabajador registrado");
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
                    photoPublicId,
                    hourlyPay);
            saved = workerRepository.findByName(request.getName());
        }


        log.info("[SUCCESS] - Trabajador registrado exitosamente: {} con ID: {}",
                saved.getName(), saved.getWorkerId());

        return WorkerResponse.builder()
                .workerId(saved.getWorkerId())
                .name(saved.getName())
                .notas(saved.getNotas())
                .salary(saved.getSalary())
                .photo(saved.getPhoto())
                .photoPublicId(saved.getPhotoPublicId())
                .hourlyPay(hourlyPay)
                .message("Trabajador registrado exitosamente")
                .success(true)
                .build();
    }

    @Transactional
    public void deleteWorker(Integer workerId) {
        log.info("[DELETE] - Intentando eliminar trabajador con ID: {}", workerId);
        WorkerRepository.WorkerProjection worker = workerRepository.findByIdWorker(workerId);
        if (worker == null) {
            log.error("[DELETE] - Trabajador no encontrado con ID: {}", workerId);
            throw new RuntimeException("Trabajador no encontrado con ID: " + workerId);
        }

        log.info("[DELETE] - Trabajador encontrado, photoPublicId: {}", worker.getPhotoPublicId());
        
        if (worker.getPhotoPublicId() != null && !worker.getPhotoPublicId().isEmpty()) {
            try {
                log.info("[DELETE] - Eliminando imagen de Cloudinary: {}", worker.getPhotoPublicId());
                imageService.deleteImage(worker.getPhotoPublicId());
                log.info("[DELETE] - Imagen eliminada exitosamente de Cloudinary");
            } catch (Exception e) {
                throw new RuntimeException("Ocurrio un error en la actualización: " + e.getMessage());
            }
        }

        workerRepository.deleteWorkerById(workerId);
        log.info("[DELETE] - Trabajador eliminado de base de datos");
    }

    @Transactional
    public Response actualizarWorker(Integer workerId, WorkerRequest request) {
        log.info("[REQUEST UPDATE] - workerId: {}, request: {}", workerId, request);

        if (workerRepository.countById(workerId) == 0) {
            throw new RuntimeException("Trabajador no encontrado con ID: " + workerId);
        }

        String notas = request.getNotas() != null ? request.getNotas() : null;
        String photo = null;
        String photoPublicId = null;

        WorkerRepository.WorkerProjection existing = workerRepository.findByIdWorker(workerId);

        if (request.getPhoto() != null && !request.getPhoto().isEmpty()) {
            photoPublicId = existing.getPhotoPublicId() != null ? existing.getPhotoPublicId() : "worker_" + workerId;
            photo = imageService.uploadImage(request.getPhoto(), photoPublicId);
        } else {
            photo = existing.getPhoto();
            photoPublicId = existing.getPhotoPublicId();
        }

        BigDecimal hourlyPay = request.getSalary().divide(BigDecimal.valueOf(56),2,RoundingMode.HALF_UP);
        

        Integer updated = workerRepository.updateWorker(
                workerId,
                request.getName(),
                notas,
                request.getSalary(),
                photo,
                photoPublicId,
                hourlyPay);

        if (updated == null || updated == 0) {
            log.error("[FAILED] - No se pudo actualizar el trabajador");
            throw new RuntimeException("No se pudo actualizar el trabajador");
        }

        WorkerRepository.WorkerProjection saved = workerRepository.findByName(request.getName());
        if (saved == null) {
            log.error("[FAILED] - Error al recuperar el trabajador actualizado: {}", request.getName());
            throw new RuntimeException("No se pudo recuperar el trabajador actualizado");
        }

        log.info("[SUCCESS] - Trabajador actualizado exitosamente: {} con ID: {}",
                saved.getName(), saved.getWorkerId());

        return Response.builder()
                .success(true)
                .message("Trabajador actualizado exitosamente")
                .data(null)
                .build();
    }
}