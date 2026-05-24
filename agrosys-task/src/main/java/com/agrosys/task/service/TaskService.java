package com.agrosys.task.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agrosys.task.dto.Response;
import com.agrosys.task.dto.SpecialTaskRequest;
import com.agrosys.task.dto.SpecialTaskResponse;
import com.agrosys.task.dto.TaskRequest;
import com.agrosys.task.dto.TaskResponse;
import com.agrosys.task.entity.SpecialTask;
import com.agrosys.task.repository.SpecialTaskRepository;
import com.agrosys.task.repository.TaskRepository;
import com.agrosys.task.repository.WorkerTaskRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final SpecialTaskRepository specialTaskRepository;
    private final WorkerTaskRepository workerTaskRepository;

    public static final int STAGE_PENDIENTE = 1;
    public static final int STAGE_EN_PROCESO = 2;
    public static final int STAGE_FINALIZADA = 3;

    @Transactional
    public TaskResponse createTask(TaskRequest request) {
        log.info("Creando tarea: {}", request.getName());

        if (request.getPlantationId() == null || request.getPlantationId() <= 0) {
            throw new IllegalArgumentException("El ID del plant\u00edo es inv\u00e1lido");
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
        log.info("Creando tarea especial: {}", request.getName());

        SpecialTask entity = new SpecialTask();
        entity.setName(request.getName());
        entity.setPaymentAmount(request.getPaymentAmount());
        entity.setCreateAt(request.getCreateAt());
        entity.setTaskStageId(request.getTaskStageId() != null ? request.getTaskStageId() : STAGE_PENDIENTE);
        entity.setEndAt(request.getEndAt());
        entity.setDescription(request.getDescription());
        entity = specialTaskRepository.save(entity);

        Integer specialTaskId = entity.getSpecialTaskId();
        log.info("Tarea especial creada con ID: {}", specialTaskId);

        if (request.getWorkerIds() != null) {
            for (Integer workerId : request.getWorkerIds()) {
                workerTaskRepository.insertWorkerAssignment(workerId, specialTaskId);
            }
        }

        return specialWithWorkersToResponse(
                specialTaskRepository.findSpecialTaskWithWorkersById(specialTaskId));
    }

    public List<SpecialTaskResponse> getAllSpecialTasks() {
        List<SpecialTaskRepository.SpecialTaskWithWorkersProjection> tasks =
                specialTaskRepository.findAllSpecialTasksWithWorkers();
        return tasks.stream().map(this::specialWithWorkersToResponse).collect(Collectors.toList());
    }

    public List<SpecialTaskResponse> getSpecialTasksByWorker(Integer workerId) {
        List<SpecialTaskRepository.SpecialTaskWithWorkersProjection> tasks =
                specialTaskRepository.findSpecialTasksByWorkerId(workerId);
        return tasks.stream().map(this::specialWithWorkersToResponse).collect(Collectors.toList());
    }

    @Transactional
    public SpecialTaskResponse updateSpecialTask(Integer specialTaskId, SpecialTaskRequest request) {
        if (specialTaskRepository.countBySpecialTaskId(specialTaskId) == 0) {
            throw new RuntimeException("Tarea especial no encontrada con id: " + specialTaskId);
        }

        specialTaskRepository.updateSpecialTask(
                specialTaskId,
                request.getName(),
                request.getPaymentAmount(),
                request.getCreateAt(),
                request.getTaskStageId() != null ? request.getTaskStageId() : STAGE_PENDIENTE,
                request.getEndAt(),
                request.getDescription());

        workerTaskRepository.deleteBySpecialTaskId(specialTaskId);
        if (request.getWorkerIds() != null) {
            for (Integer workerId : request.getWorkerIds()) {
                workerTaskRepository.insertWorkerAssignment(workerId, specialTaskId);
            }
        }

        return specialWithWorkersToResponse(
                specialTaskRepository.findSpecialTaskWithWorkersById(specialTaskId));
    }

    @Transactional
    public Response deleteSpecialTask(Integer specialTaskId) {
        if (specialTaskRepository.countBySpecialTaskId(specialTaskId) == 0) {
            throw new RuntimeException("Tarea especial no encontrada con id: " + specialTaskId);
        }
        workerTaskRepository.deleteBySpecialTaskId(specialTaskId);
        specialTaskRepository.deleteSpecialTaskById(specialTaskId);
        log.info("Tarea especial eliminada con ID: {}", specialTaskId);
        return Response.builder()
                .success(true)
                .message("Tarea especial eliminada exitosamente")
                .build();
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

    private SpecialTaskResponse specialWithWorkersToResponse(
            SpecialTaskRepository.SpecialTaskWithWorkersProjection proj) {
        List<Integer> workerIds = Collections.emptyList();
        if (proj.getWorkerIds() != null && !proj.getWorkerIds().isEmpty()) {
            workerIds = Arrays.stream(proj.getWorkerIds().split(","))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        }
        return SpecialTaskResponse.builder()
                .specialTaskId(proj.getSpecialTaskId())
                .name(proj.getName())
                .paymentAmount(proj.getPaymentAmount())
                .createAt(proj.getCreateAt())
                .taskStageId(proj.getTaskStageId())
                .endAt(proj.getEndAt())
                .description(proj.getDescription())
                .workerIds(workerIds)
                .build();
    }
}
