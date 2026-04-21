package com.agrosys.web.controller.worker;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.agrosys.web.utils.GatewayClient;
import com.agrosys.web.utils.JwtHelper;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/workers")
@RequiredArgsConstructor
@Slf4j
public class workerController {

    private final GatewayClient gatewayClient;
    private final JwtHelper jwtHelper;

    @GetMapping
    public String workersPage(
            @RequestParam(required = false, defaultValue = "usuarios") String tab,
            HttpSession session, Model model) {

        List<String> modules = jwtHelper.getUserModules(session);
        log.info("[MODULES] - Módulos del usuario: {}", modules);

        if (!modules.contains("MODULE_TRABAJADORES")) {
            return "redirect:/access-denied";
        }

        model.addAttribute("modules", modules);
        model.addAttribute("activeTab", tab);
        return "home/workers/worker";
    }

    @GetMapping("/get-all")
    @ResponseBody
    public ResponseEntity<Object> getAllWorkers(HttpSession session) {
        try {
            String token = (String) session.getAttribute("JWT_TOKEN");

            Object response = gatewayClient.get(
                    "/api/workers/get-all",
                    Object.class,
                    token);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("[FAILED] - Error al obtener workers: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error al obtener workers"));
        }
    }

    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<Object> registerWorker(@RequestBody Map<String, String> body, HttpSession session) {
        try {
            String token = (String) session.getAttribute("JWT_TOKEN");

            Object response = gatewayClient.post(
                    "/api/workers/register",
                    body,
                    Object.class,
                    token);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("[FAILED] - Error al registrar worker: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error al registrar worker"));
        }
    }

    @DeleteMapping("/delete")
    @ResponseBody
    public ResponseEntity<Object> deleteWorker(@RequestBody Map<String, Integer> body, HttpSession session) {
        try {
            String token = (String) session.getAttribute("JWT_TOKEN");
            Integer workerId = body.get("userId");
            log.info("Intentando eliminar worker con ID: {}", workerId);

            if (workerId == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "ID del worker no proporcionado"));
            }

            Map<String, Integer> requestBody = Map.of("id", workerId);

            Object response = gatewayClient.delete(
                    "/api/workers/delete",
                    requestBody,
                    Object.class,
                    token);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("[FAILED] - Error al eliminar worker: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error al eliminar worker: " + e.getMessage()));
        }
    }

    @PutMapping("/update")
    @ResponseBody
    public ResponseEntity<Object> updateWorker(@RequestBody Map<String, Object> body, HttpSession session) {
        try {
            String token = (String) session.getAttribute("JWT_TOKEN");

            Object response = gatewayClient.put(
                    "/api/workers/update",
                    body,
                    Object.class,
                    token);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("[FAILED] - Error al actualizar worker: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error al actualizar worker"));
        }
    }
}