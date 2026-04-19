package com.agrosys.task.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agrosys.task.dto.TaskRequest;
import com.agrosys.task.dto.TaskResponse;
import com.agrosys.task.repository.TaskRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;

    @Transactional
    public TaskResponse createTask(TaskRequest request) {
        log.info("Creando tarea para plantío {}: {}", request.getPlantationId(), request);

        // Validación opcional: verificar si ya existe una tarea con el mismo nombre (si aplica)
        Integer existingId = taskRepository.existsByName(request.getName());
        if (existingId != null) {
            throw new RuntimeException("Ya existe una tarea con el nombre: " + request.getName());
        }

        Integer inserted = taskRepository.insertTask(
                request.getName(),
                request.getDescription(),
                request.getCreateAt(),
                request.getEndAt(),
                request.getTaskStageId(),
                request.getPlantationId()
        );

        if (inserted == null || inserted == 0) {
            throw new RuntimeException("No se pudo crear la tarea");
        }
        TaskRepository.TaskProjection saved = taskRepository.findTaskById(
                getLastInsertedId()
        );
        if (saved == null) {
            throw new RuntimeException("No se pudo recuperar la tarea creada");
        }

        log.info("Tarea creada con ID: {}", saved.getTaskId());
        return mapToResponse(saved);
    }

    // Método auxiliar para obtener el último ID insertado (usando la conexión actual)
    // Se puede implementar en el repositorio con una consulta nativa.
    private Integer getLastInsertedId() {

        return null;
    }

    public List<TaskResponse> getAllTasks() {
        List<TaskRepository.TaskProjection> tasks = taskRepository.findAllTasks();
        return tasks.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public TaskResponse getTaskById(Integer id) {
        TaskRepository.TaskProjection task = taskRepository.findTaskById(id);
        if (task == null) {
            throw new RuntimeException("Tarea no encontrada con id: " + id);
        }
        return mapToResponse(task);
    }

    @Transactional
    public TaskResponse updateTask(Integer id, TaskRequest request) {
        // Verificar existencia
        if (taskRepository.countById(id) == 0) {
            throw new RuntimeException("Tarea no encontrada con id: " + id);
        }
        int updated = taskRepository.updateTask(id, request.getName(), request.getDescription(),
                request.getCreateAt(), request.getEndAt(), request.getTaskStageId(), request.getPlantationId());
        if (updated == 0) {
            throw new RuntimeException("No se pudo actualizar la tarea");
        }
        return getTaskById(id);
    }

    @Transactional
    public void deleteTask(Integer id) {
        if (taskRepository.countById(id) == 0) {
            throw new RuntimeException("Tarea no encontrada con id: " + id);
        }
        taskRepository.deleteTaskById(id);
        log.info("Tarea eliminada con ID: {}", id);
    }

    private TaskResponse mapToResponse(TaskRepository.TaskProjection proj) {
        return TaskResponse.builder()
                .taskId(proj.getTaskId())
                .name(proj.getName())
                .description(proj.getDescription())
                .createAt(proj.getCreateAt())
                .endAt(proj.getEndAt())
                .taskStageId(proj.getTaskStageId())
                .plantationId(proj.getPlantationId())
                .build();
    }
}
