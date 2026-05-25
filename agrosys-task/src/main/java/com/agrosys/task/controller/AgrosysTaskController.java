package com.agrosys.task.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.agrosys.task.dto.Response;
import com.agrosys.task.dto.SpecialTaskRequest;
import com.agrosys.task.dto.SpecialTaskResponse;
import com.agrosys.task.dto.TaskRequest;
import com.agrosys.task.dto.TaskResponse;
import com.agrosys.task.service.TaskService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Slf4j
public class AgrosysTaskController {

    private final TaskService taskService;

    @PostMapping("/register")
    @PreAuthorize("hasAuthority('MODULE_TAREAS')")
    public ResponseEntity<Response> createTask(@Valid @RequestBody TaskRequest request) {
        log.info("[REQUEST CREATE] - request: {}", request);
        try {
            TaskResponse response = taskService.createTask(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Response.builder()
                            .success(true)
                            .message("Tarea creada exitosamente")
                            .data(response)
                            .build());
        } catch (Exception e) {
            log.error("[ERROR] - Error al crear tarea: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(Response.builder()
                            .success(false)
                            .message("Error al crear tarea: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/get-all")
    @PreAuthorize("hasAuthority('MODULE_TAREAS')")
    public ResponseEntity<Response> getAllTasks() {
        try {
            List<TaskResponse> tasks = taskService.getAllTasks();
            return ResponseEntity.ok(Response.builder()
                    .success(true)
                    .message("Tareas obtenidas exitosamente")
                    .data(tasks)
                    .build());
        } catch (Exception e) {
            log.error("[ERROR] - Error al obtener tareas: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(Response.builder()
                            .success(false)
                            .message("Error al obtener tareas: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('MODULE_TAREAS')")
    public ResponseEntity<Response> getTaskById(@PathVariable Integer id) {
        log.info("[REQUEST GET] - id: {}", id);
        try {
            TaskResponse task = taskService.getTaskById(id);
            return ResponseEntity.ok(Response.builder()
                    .success(true)
                    .message("Tarea obtenida exitosamente")
                    .data(task)
                    .build());
        } catch (IllegalArgumentException e) {
            log.error("[ERROR] - Tarea no encontrada: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.builder()
                            .success(false)
                            .message("Tarea no encontrada con id: " + id)
                            .build());
        } catch (Exception e) {
            log.error("[ERROR] - Error al obtener tarea: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(Response.builder()
                            .success(false)
                            .message("Error al obtener tarea: " + e.getMessage())
                            .build());
        }
    }

    @PutMapping("/edit/{id}")
    @PreAuthorize("hasAuthority('MODULE_TAREAS')")
    public ResponseEntity<Response> updateTask(@PathVariable Integer id, @Valid @RequestBody TaskRequest request) {
        log.info("[REQUEST UPDATE] - id: {}, request: {}", id, request);
        try {
            TaskResponse updated = taskService.updateTask(id, request);
            return ResponseEntity.ok(Response.builder()
                    .success(true)
                    .message("Tarea actualizada exitosamente")
                    .data(updated)
                    .build());
        } catch (Exception e) {
            log.error("[ERROR] - Error al actualizar tarea: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(Response.builder()
                            .success(false)
                            .message("Error al actualizar tarea: " + e.getMessage())
                            .build());
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('MODULE_TAREAS')")
    public ResponseEntity<Response> deleteTask(@PathVariable Integer id) {
        log.info("[REQUEST DELETE] - id: {}", id);
        try {
            taskService.deleteTask(id);
            return ResponseEntity.ok(Response.builder()
                    .success(true)
                    .message("Tarea eliminada exitosamente")
                    .build());
        } catch (Exception e) {
            log.error("[ERROR] - Error al eliminar tarea: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(Response.builder()
                            .success(false)
                            .message("Error al eliminar tarea: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/stages")
    @PreAuthorize("hasAuthority('MODULE_TAREAS')")
    public ResponseEntity<Response> getStages() {
        try {
            return ResponseEntity.ok(Response.builder()
                    .success(true)
                    .message("Estados obtenidos exitosamente")
                    .data(taskService.getStages())
                    .build());
        } catch (Exception e) {
            log.error("[ERROR] - Error al obtener estados: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(Response.builder()
                            .success(false)
                            .message("Error al obtener estados: " + e.getMessage())
                            .build());
        }
    }

    @PutMapping("/status/{id}")
    @PreAuthorize("hasAuthority('MODULE_TAREAS')")
    public ResponseEntity<Response> updateTaskStage(@PathVariable Integer id,
            @RequestParam Integer stage) {
        log.info("[REQUEST STATUS] - id: {}, stage: {}", id, stage);
        try {
            return ResponseEntity.ok(taskService.updateTaskStage(id, stage));
        } catch (IllegalArgumentException e) {
            log.error("[ERROR] - Tarea no encontrada: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.builder()
                            .success(false)
                            .message("Tarea no encontrada con id: " + id)
                            .build());
        } catch (Exception e) {
            log.error("[ERROR] - Error al actualizar estado: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(Response.builder()
                            .success(false)
                            .message("Error al actualizar estado: " + e.getMessage())
                            .build());
        }
    }

    // Special Tasks

    @PostMapping("/special/register")
    @PreAuthorize("hasAuthority('MODULE_TAREAS')")
    public ResponseEntity<Response> createSpecialTask(@Valid @RequestBody SpecialTaskRequest request) {
        log.info("[REQUEST SPECIAL CREATE] - request: {}", request);
        try {
            SpecialTaskResponse response = taskService.createSpecialTask(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Response.builder()
                            .success(true)
                            .message("Tarea especial creada exitosamente")
                            .data(response)
                            .build());
        } catch (IllegalArgumentException e) {
            log.error("[ERROR] - Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Response.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("[ERROR] - Error al crear tarea especial: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(Response.builder()
                            .success(false)
                            .message("Error al crear tarea especial: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/special/get-all")
    @PreAuthorize("hasAuthority('MODULE_TAREAS')")
    public ResponseEntity<Response> getAllSpecialTasks() {
        try {
            List<SpecialTaskResponse> tasks = taskService.getAllSpecialTasks();
            return ResponseEntity.ok(Response.builder()
                    .success(true)
                    .message("Tareas especiales obtenidas exitosamente")
                    .data(tasks)
                    .build());
        } catch (Exception e) {
            log.error("[ERROR] - Error al obtener tareas especiales: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(Response.builder()
                            .success(false)
                            .message("Error al obtener tareas especiales: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/special/by-worker/{workerId}")
    @PreAuthorize("hasAuthority('MODULE_TAREAS')")
    public ResponseEntity<Response> getSpecialTasksByWorker(@PathVariable Integer workerId) {
        log.info("[REQUEST SPECIAL BY WORKER] - workerId: {}", workerId);
        try {
            List<SpecialTaskResponse> tasks = taskService.getSpecialTasksByWorker(workerId);
            return ResponseEntity.ok(Response.builder()
                    .success(true)
                    .message("Tareas especiales del worker obtenidas exitosamente")
                    .data(tasks)
                    .build());
        } catch (Exception e) {
            log.error("[ERROR] - Error al obtener tareas del worker: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(Response.builder()
                            .success(false)
                            .message("Error al obtener tareas del worker: " + e.getMessage())
                            .build());
        }
    }

    @PutMapping("/special/edit/{taskId}")
    @PreAuthorize("hasAuthority('MODULE_TAREAS')")
    public ResponseEntity<Response> updateSpecialTask(@PathVariable Integer taskId,
            @Valid @RequestBody SpecialTaskRequest request) {
        log.info("[REQUEST SPECIAL UPDATE] - taskId: {}, request: {}", taskId, request);
        try {
            SpecialTaskResponse updated = taskService.updateSpecialTask(taskId, request);
            return ResponseEntity.ok(Response.builder()
                    .success(true)
                    .message("Tarea especial actualizada exitosamente")
                    .data(updated)
                    .build());
        } catch (IllegalArgumentException e) {
            log.error("[ERROR] - Tarea especial no encontrada: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.builder()
                            .success(false)
                            .message("Tarea especial no encontrada con id: " + taskId)
                            .build());
        } catch (Exception e) {
            log.error("[ERROR] - Error al actualizar tarea especial: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(Response.builder()
                            .success(false)
                            .message("Error al actualizar tarea especial: " + e.getMessage())
                            .build());
        }
    }

    @DeleteMapping("/special/delete/{taskId}")
    @PreAuthorize("hasAuthority('MODULE_TAREAS')")
    public ResponseEntity<Response> deleteSpecialTask(@PathVariable Integer taskId) {
        log.info("[REQUEST SPECIAL DELETE] - taskId: {}", taskId);
        try {
            taskService.deleteSpecialTask(taskId);
            return ResponseEntity.ok(Response.builder()
                    .success(true)
                    .message("Tarea especial eliminada exitosamente")
                    .build());
        } catch (Exception e) {
            log.error("[ERROR] - Error al eliminar tarea especial: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(Response.builder()
                            .success(false)
                            .message("Error al eliminar tarea especial: " + e.getMessage())
                            .build());
        }
    }

    /*
     * === ENDPOINT PARA EL GRAFICO ===
     * 
     * @GetMapping("/summary")
     * 
     * @PreAuthorize("hasAuthority('MODULE_TAREAS')")
     * public ResponseEntity<Response> getTaskSummary() {
     * try {
     * return ResponseEntity.ok(Response.builder()
     * .success(true)
     * .message("Resumen de tareas obtenido exitosamente")
     * .data(taskService.getTaskSummary())
     * .build());
     * } catch (Exception e) {
     * log.error("[ERROR] - Error al obtener resumen de tareas: {}", e.getMessage(),
     * e);
     * return ResponseEntity.badRequest()
     * .body(Response.builder()
     * .success(false)
     * .message("Error al obtener resumen: " + e.getMessage())
     * .build());
     * }
     * }
     * 
     * @GetMapping("/summary/by-worker")
     * 
     * @PreAuthorize("hasAuthority('MODULE_TAREAS')")
     * public ResponseEntity<Response> getSpecialTaskSummaryByWorker() {
     * try {
     * return ResponseEntity.ok(Response.builder()
     * .success(true)
     * .message("Resumen por trabajador obtenido exitosamente")
     * .data(taskService.getSpecialTaskSummaryByWorker())
     * .build());
     * } catch (Exception e) {
     * log.error("[ERROR] - Error al obtener resumen por trabajador: {}",
     * e.getMessage(), e);
     * return ResponseEntity.badRequest()
     * .body(Response.builder()
     * .success(false)
     * .message("Error al obtener resumen por trabajador: " + e.getMessage())
     * .build());
     * }
     * }
     */
    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('MODULE_TAREAS')")
    public ResponseEntity<Response> getTaskSummary(
            @RequestParam Integer anio,
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer dia) {
        try {
            return ResponseEntity.ok(Response.builder()
                    .success(true)
                    .message("Resumen de tareas obtenido exitosamente")
                    .data(taskService.getTaskSummaryFiltered(anio, mes, dia))
                    .build());
        } catch (Exception e) {
            log.error("[ERROR] - Error al obtener resumen de tareas: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(Response.builder()
                            .success(false)
                            .message("Error al obtener resumen: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/summary/by-worker")
    @PreAuthorize("hasAuthority('MODULE_TAREAS')")
    public ResponseEntity<Response> getSpecialTaskSummaryByWorker(
            @RequestParam Integer anio,
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer dia) {
        try {
            return ResponseEntity.ok(Response.builder()
                    .success(true)
                    .message("Resumen por trabajador obtenido exitosamente")
                    .data(taskService.getSpecialTaskSummaryByWorkerFiltered(anio, mes, dia))
                    .build());
        } catch (Exception e) {
            log.error("[ERROR] - Error al obtener resumen por trabajador: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(Response.builder()
                            .success(false)
                            .message("Error al obtener resumen por trabajador: " + e.getMessage())
                            .build());
        }
    }

}