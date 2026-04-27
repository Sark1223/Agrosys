package com.agrosys.task.controller;

import com.agrosys.task.dto.Response;
import com.agrosys.task.dto.SpecialTaskRequest;
import com.agrosys.task.dto.SpecialTaskResponse;
import com.agrosys.task.dto.TaskRequest;
import com.agrosys.task.dto.TaskResponse;
import com.agrosys.task.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Slf4j
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/register")
    @PreAuthorize("hasAuthority('MODULE_TAREAS')")
    public ResponseEntity<Response> createTask(@Valid @RequestBody TaskRequest request) {
        TaskResponse response = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.builder()
                        .success(true)
                        .message("Tarea creada exitosamente")
                        .data(response)
                        .build());
    }

    @GetMapping("/get-all")
    @PreAuthorize("hasAuthority('MODULE_TAREAS')")
    public ResponseEntity<Response> getAllTasks() {
        List<TaskResponse> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(Response.builder()
                .success(true)
                .message("Tareas obtenidas exitosamente")
                .data(tasks)
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('MODULE_TAREAS')")
    public ResponseEntity<Response> getTaskById(@PathVariable Integer id) {
        TaskResponse task = taskService.getTaskById(id);
        return ResponseEntity.ok(Response.builder()
                .success(true)
                .message("Tarea obtenida exitosamente")
                .data(task)
                .build());
    }

    @PutMapping("/edit/{id}")
    @PreAuthorize("hasAuthority('MODULE_TAREAS')")
    public ResponseEntity<Response> updateTask(@PathVariable Integer id, @Valid @RequestBody TaskRequest request) {
        TaskResponse updated = taskService.updateTask(id, request);
        return ResponseEntity.ok(Response.builder()
                .success(true)
                .message("Tarea actualizada exitosamente")
                .data(updated)
                .build());
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('MODULE_TAREAS')")
    public ResponseEntity<Response> deleteTask(@PathVariable Integer id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok(Response.builder()
                .success(true)
                .message("Tarea eliminada exitosamente")
                .build());
    }

    @GetMapping("/stages")
    @PreAuthorize("hasAuthority('MODULE_TAREAS')")
    public ResponseEntity<Response> getStages() {
        return ResponseEntity.ok(Response.builder()
                .success(true)
                .message("Estados obtenidos exitosamente")
                .data(taskService.getStages())
                .build());
    }

    @PutMapping("/status/{id}")
    @PreAuthorize("hasAuthority('MODULE_TAREAS')")
    public ResponseEntity<Response> updateTaskStage(@PathVariable Integer id, @RequestParam Integer stage) {
        return ResponseEntity.ok(taskService.updateTaskStage(id, stage));
    }

    // Special Tasks

    @PostMapping("/special/register")
    @PreAuthorize("hasAuthority('MODULE_TAREAS')")
    public ResponseEntity<Response> createSpecialTask(@Valid @RequestBody SpecialTaskRequest request) {
        SpecialTaskResponse response = taskService.createSpecialTask(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.builder()
                        .success(true)
                        .message("Tarea especial creada exitosamente")
                        .data(response)
                        .build());
    }

    @GetMapping("/special/get-all")
    @PreAuthorize("hasAuthority('MODULE_TAREAS')")
    public ResponseEntity<Response> getAllSpecialTasks() {
        List<SpecialTaskResponse> tasks = taskService.getAllSpecialTasks();
        return ResponseEntity.ok(Response.builder()
                .success(true)
                .message("Tareas especiales obtenidas exitosamente")
                .data(tasks)
                .build());
    }

    @GetMapping("/special/by-worker/{workerId}")
    @PreAuthorize("hasAuthority('MODULE_TAREAS')")
    public ResponseEntity<Response> getSpecialTasksByWorker(@PathVariable Integer workerId) {
        List<SpecialTaskResponse> tasks = taskService.getSpecialTasksByWorker(workerId);
        return ResponseEntity.ok(Response.builder()
                .success(true)
                .message("Tareas especiales del worker obtenidas exitosamente")
                .data(tasks)
                .build());
    }

    @PutMapping("/special/edit/{taskId}")
    @PreAuthorize("hasAuthority('MODULE_TAREAS')")
    public ResponseEntity<Response> updateSpecialTask(@PathVariable Integer taskId, 
            @Valid @RequestBody SpecialTaskRequest request) {
        SpecialTaskResponse updated = taskService.updateSpecialTask(taskId, request);
        return ResponseEntity.ok(Response.builder()
                .success(true)
                .message("Tarea especial actualizada exitosamente")
                .data(updated)
                .build());
    }
}