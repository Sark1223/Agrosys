package com.agrosys.task.controller;

import com.agrosys.task.dto.Response;
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
}