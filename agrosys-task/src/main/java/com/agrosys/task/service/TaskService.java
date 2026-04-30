package com.agrosys.task.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agrosys.task.dto.Response;
import com.agrosys.task.dto.SpecialTaskRequest;
import com.agrosys.task.dto.SpecialTaskResponse;
import com.agrosys.task.dto.TaskRequest;
import com.agrosys.task.dto.TaskResponse;
import com.agrosys.task.repository.SpecialTaskRepository;
import com.agrosys.task.repository.TaskRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final SpecialTaskRepository specialTaskRepository;

    public static final int STAGE_PENDIENTE = 1;
    public static final int STAGE_EN_PROCESO = 2;
    public static final int STAGE_FINALIZADA = 3;

    @Transactional
    public TaskResponse createTask(TaskRequest request) {
        log.info("Creando tarea: {}", request.getName());

        if (request.getPlantationId() == null || request.getPlantationId() <= 0) {
            throw new IllegalArgumentException("El ID del plantío es inválido");
        }

        Integer inserted = taskRepository.insertTask(
                request.getName(),
                request.getDescription(),
                request.getCreateAt(),
                request.getEndAt(),
                STAGE_PENDIENTE,
                request.getPlantationId()
        );

        if (inserted == null || inserted == 0) {
            throw new RuntimeException("No se pudo crear la tarea");
        }

        Integer newTaskId = taskRepository.getLastInsertId();
        TaskRepository.TaskProjection saved = taskRepository.findTaskById(newTaskId);
        
        log.info("Tarea creada con ID: {}", saved.getTaskId());
        return mapToResponse(saved);
    }

    public List<TaskResponse> getAllTasks() {
        List<TaskRepository.TaskProjection> tasks = taskRepository.findAllTasks();
        return tasks.stream().map(this::mapToResponse).collect(Collectors.toList());
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
        if (taskRepository.countById(id) == 0) {
            throw new RuntimeException("Tarea no encontrada con id: " + id);
        }
        taskRepository.updateTask(id, request.getName(), request.getDescription(),
                request.getCreateAt(), request.getEndAt(), request.getTaskStageId(), 
                request.getPlantationId());
        return getTaskById(id);
    }

    @Transactional
    public void deleteTask(Integer id) {
        if (taskRepository.countById(id) == 0) {
            throw new RuntimeException("Tarea no encontrada con id: " + id);
        }
        specialTaskRepository.deleteSpecialTaskById(id);
        taskRepository.deleteTaskById(id);
        log.info("Tarea eliminada con ID: {}", id);
    }

    @Transactional
    public Response updateTaskStage(Integer id, Integer taskStageId) {
        if (taskRepository.countById(id) == 0) {
            throw new RuntimeException("Tarea no encontrada con id: " + id);
        }
        taskRepository.updateTaskStage(id, taskStageId);
        return Response.builder()
                .success(true)
                .message("Estado actualizado exitosamente")
                .build();
    }

    public List<Response> getStages() {
        return List.of(
            Response.builder().success(true).message("1").data("Pendiente").build(),
            Response.builder().success(true).message("2").data("En Proceso").build(),
            Response.builder().success(true).message("3").data("Finalizada").build()
        );
    }

    // Special Tasks

    @Transactional
    public SpecialTaskResponse createSpecialTask(SpecialTaskRequest request) {
        log.info("Creando tarea especial para worker: {}", request.getWorkerId());

        if (taskRepository.countById(request.getTaskId()) == 0) {
            throw new RuntimeException("Tarea no encontrada con id: " + request.getTaskId());
        }

        Integer inserted = specialTaskRepository.insertSpecialTask(
                request.getTaskId(),
                request.getWorkerId(),
                request.getPaymentAmount()
        );

        if (inserted == null || inserted == 0) {
            throw new RuntimeException("No se pudo crear la tarea especial");
        }

        specialTaskRepository.updateSpecialTask(request.getTaskId(), request.getWorkerId(), request.getPaymentAmount());

        return specialTaskToResponse(request.getTaskId());
    }

    public List<SpecialTaskResponse> getAllSpecialTasks() {
        List<SpecialTaskRepository.SpecialTaskProjection> tasks = specialTaskRepository.findAllSpecialTasks();
        return tasks.stream().map(this::specialProjectionToResponse).collect(Collectors.toList());
    }

    public List<SpecialTaskResponse> getSpecialTasksByWorker(Integer workerId) {
        List<SpecialTaskRepository.SpecialTaskProjection> tasks = specialTaskRepository.findByWorkerId(workerId);
        return tasks.stream().map(this::specialProjectionToResponse).collect(Collectors.toList());
    }

    @Transactional
    public SpecialTaskResponse updateSpecialTask(Integer taskId, SpecialTaskRequest request) {
        if (specialTaskRepository.countByTaskId(taskId) == 0) {
            throw new RuntimeException("Tarea especial no encontrada con id: " + taskId);
        }
        specialTaskRepository.updateSpecialTask(taskId, request.getWorkerId(), request.getPaymentAmount());
        return specialTaskToResponse(taskId);
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

    private SpecialTaskResponse specialTaskToResponse(Integer taskId) {
        SpecialTaskRepository.SpecialTaskProjection proj = specialTaskRepository.findByTaskId(taskId);
        if (proj == null) {
            throw new RuntimeException("Tarea especial no encontrada");
        }
        return specialProjectionToResponse(proj);
    }

    private SpecialTaskResponse specialProjectionToResponse(SpecialTaskRepository.SpecialTaskProjection proj) {
        return SpecialTaskResponse.builder()
                .taskId(proj.getTaskId())
                .workerId(proj.getWorkerId())
                .paymentAmount(proj.getPaymentAmount())
                .taskStageId(proj.getTaskStageId())
                .build();
    }
}