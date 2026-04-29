package com.agrosys.web.controller.task;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.agrosys.web.dto.Response;
import com.agrosys.web.dto.task.TaskRequest;
import com.agrosys.web.dto.task.SpecialTaskRequest;
import com.agrosys.web.utils.GatewayClient;
import com.agrosys.web.utils.JwtHelper;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Slf4j
public class TaskController {

    private final GatewayClient gatewayClient;
    private final JwtHelper jwtHelper;

    @GetMapping
    public String tasksPage(
            @RequestParam(required = false, defaultValue = "generales") String tab,
            HttpSession session, Model model) {

        List<String> modules = jwtHelper.getUserModules(session);
        log.info("[MODULES] - Módulos del usuario: {}", modules);

        if (!modules.contains("MODULE_TAREAS")) {
            return "redirect:/access-denied";
        }

        model.addAttribute("modules", modules);
        model.addAttribute("activeTab", tab);
        return "home/tasks/tasks";
    }

    @GetMapping("/get-all")
    @ResponseBody
    public ResponseEntity<Response> getAllTasks(HttpSession session) {
        try {
            List<String> modules = jwtHelper.getUserModules(session);
            if (!modules.contains("MODULE_TAREAS")) {
                return ResponseEntity.status(403).build();
            }

            String token = (String) session.getAttribute("JWT_TOKEN");
            Response response = gatewayClient.get(
                    "/api/tasks/get-all",
                    Response.class,
                    token);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("[FAILED] - Error al obtener tareas: {}", e.getMessage());
            throw new RuntimeException("Error al obtener tareas: " + e.getMessage());
        }
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<Response> registerTask(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String createAt,
            @RequestParam(required = false) String endAt,
            @RequestParam Integer plantationId,
            HttpSession session) {

        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_TAREAS")) {
            return ResponseEntity.status(403).build();
        }

TaskRequest request = new TaskRequest();
        request.setName(name);
        request.setDescription(description);
        request.setCreateAt(java.time.LocalDate.parse(createAt));
        if (endAt != null && !endAt.isEmpty()) {
            request.setEndAt(java.time.LocalDate.parse(endAt));
        }
        request.setPlantationId(plantationId);

        String token = (String) session.getAttribute("JWT_TOKEN");
        Response response = gatewayClient.post(
                "/api/tasks/register",
                request,
                Response.class,
                token);

        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/edit/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<Response> updateTask(@PathVariable Integer id,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String createAt,
            @RequestParam(required = false) String endAt,
            @RequestParam Integer taskStageId,
            @RequestParam Integer plantationId,
            HttpSession session) {

        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_TAREAS")) {
            return ResponseEntity.status(403).build();
        }

        TaskRequest request = new TaskRequest();
        request.setName(name);
        request.setDescription(description);
        request.setCreateAt(java.time.LocalDate.parse(createAt));
        if (endAt != null && !endAt.isEmpty()) {
            request.setEndAt(java.time.LocalDate.parse(endAt));
        }
        request.setTaskStageId(taskStageId);
        request.setPlantationId(plantationId);

        String token = (String) session.getAttribute("JWT_TOKEN");
        Response response = gatewayClient.put(
                "/api/tasks/edit/" + id,
                request,
                Response.class,
                token);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<Response> deleteTask(@PathVariable Integer id, HttpSession session) {
        try {
            List<String> modules = jwtHelper.getUserModules(session);
            if (!modules.contains("MODULE_TAREAS")) {
                return ResponseEntity.status(403).build();
            }

            String token = (String) session.getAttribute("JWT_TOKEN");
            log.info("[REQUEST DELETE] - Eliminando tarea con ID: {}", id);

            Response response = gatewayClient.delete(
                    "/api/tasks/delete/" + id,
                    Response.class,
                    token);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("[FAILED] - Error al eliminar tarea: {}", e.getMessage());
            throw new RuntimeException("Error al eliminar tarea: " + e.getMessage());
        }
    }

    @GetMapping("/stages")
    @ResponseBody
    public ResponseEntity<Response> getStages(HttpSession session) {
        try {
            List<String> modules = jwtHelper.getUserModules(session);
            if (!modules.contains("MODULE_TAREAS")) {
                return ResponseEntity.status(403).build();
            }

            String token = (String) session.getAttribute("JWT_TOKEN");
            Response response = gatewayClient.get(
                    "/api/tasks/stages",
                    Response.class,
                    token);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("[FAILED] - Error al obtener estados: {}", e.getMessage());
            throw new RuntimeException("Error al obtener estados: " + e.getMessage());
        }
    }

    @PutMapping("/status/{id}")
    @ResponseBody
    public ResponseEntity<Response> updateTaskStatus(@PathVariable Integer id,
            @RequestParam Integer stage,
            HttpSession session) {
        try {
            List<String> modules = jwtHelper.getUserModules(session);
            if (!modules.contains("MODULE_TAREAS")) {
                return ResponseEntity.status(403).build();
            }

            String token = (String) session.getAttribute("JWT_TOKEN");
            log.info("[REQUEST STATUS] - Actualizando tarea {} a estado {}", id, stage);

            Response response = gatewayClient.put(
                    "/api/tasks/status/" + id + "?stage=" + stage,
                    null,
                    Response.class,
                    token);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("[FAILED] - Error al actualizar estado: {}", e.getMessage());
            throw new RuntimeException("Error al actualizar estado: " + e.getMessage());
        }
    }

    // Special Tasks

    @PostMapping(value = "/special/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<Response> registerSpecialTask(
            @RequestParam Integer taskId,
            @RequestParam Integer workerId,
            @RequestParam java.math.BigDecimal paymentAmount,
            HttpSession session) {

        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_TAREAS")) {
            return ResponseEntity.status(403).build();
        }

        SpecialTaskRequest request = new SpecialTaskRequest();
        request.setTaskId(taskId);
        request.setWorkerId(workerId);
        request.setPaymentAmount(paymentAmount);

        String token = (String) session.getAttribute("JWT_TOKEN");
        Response response = gatewayClient.post(
                "/api/tasks/special/register",
                request,
                Response.class,
                token);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/special/get-all")
    @ResponseBody
    public ResponseEntity<Response> getAllSpecialTasks(HttpSession session) {
        try {
            List<String> modules = jwtHelper.getUserModules(session);
            if (!modules.contains("MODULE_TAREAS")) {
                return ResponseEntity.status(403).build();
            }

            String token = (String) session.getAttribute("JWT_TOKEN");
            Response response = gatewayClient.get(
                    "/api/tasks/special/get-all",
                    Response.class,
                    token);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("[FAILED] - Error al obtener tareas especiales: {}", e.getMessage());
            throw new RuntimeException("Error al obtener tareas especiales: " + e.getMessage());
        }
    }

    @GetMapping("/special/by-worker/{workerId}")
    @ResponseBody
    public ResponseEntity<Response> getSpecialTasksByWorker(@PathVariable Integer workerId, HttpSession session) {
        try {
            List<String> modules = jwtHelper.getUserModules(session);
            if (!modules.contains("MODULE_TAREAS")) {
                return ResponseEntity.status(403).build();
            }

            String token = (String) session.getAttribute("JWT_TOKEN");
            Response response = gatewayClient.get(
                    "/api/tasks/special/by-worker/" + workerId,
                    Response.class,
                    token);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("[FAILED] - Error al obtener tareas del worker: {}", e.getMessage());
            throw new RuntimeException("Error al obtener tareas del worker: " + e.getMessage());
        }
    }

    @PutMapping(value = "/special/edit/{taskId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<Response> updateSpecialTask(@PathVariable Integer taskId,
            @RequestParam Integer workerId,
            @RequestParam java.math.BigDecimal paymentAmount,
            HttpSession session) {

        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_TAREAS")) {
            return ResponseEntity.status(403).build();
        }

        SpecialTaskRequest request = new SpecialTaskRequest();
        request.setTaskId(taskId);
        request.setWorkerId(workerId);
        request.setPaymentAmount(paymentAmount);

        String token = (String) session.getAttribute("JWT_TOKEN");
        Response response = gatewayClient.put(
                "/api/tasks/special/edit/" + taskId,
                request,
                Response.class,
                token);

        return ResponseEntity.ok(response);
    }
}