package com.agrosys.web.controller.worker;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.agrosys.web.dto.Response;
import com.agrosys.web.dto.worker.WorkerRequest;
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
            @RequestParam(required = false, defaultValue = "workers") String tab,
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
    public ResponseEntity<Response> getAllWorkers(HttpSession session) {
        try {
            List<String> modules = jwtHelper.getUserModules(session);
            if (!modules.contains("MODULE_TRABAJADORES")) {
                return ResponseEntity.status(403).build();
            }

            String token = (String) session.getAttribute("JWT_TOKEN");

            Response response = gatewayClient.get(
                    "/api/workers/get-all",
                    Response.class,
                    token);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("[FAILED] - Error al obtener workers: {}", e.getMessage());
            throw new RuntimeException("Error al obtener workers: " + e.getMessage());
        }
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<Response> registerWorker(
            @RequestParam String nombre,
            @RequestParam BigDecimal salary,
            @RequestParam(required = false) String notas,
            @RequestParam(required = false) MultipartFile photo,
            HttpSession session) {

        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_TRABAJADORES")) {
            return ResponseEntity.status(403).build();
        }

        String photoString = null;
        if (photo != null && !photo.isEmpty()) {
            try {
                byte[] photoBytes = photo.getBytes();
                photoString = java.util.Base64.getEncoder().encodeToString(photoBytes);
            } catch (IOException e) {
                log.error("Error al convertir la foto: {}", e.getMessage());
                throw new RuntimeException("Error al convertir la foto: " + e.getMessage());
            }
        }

        WorkerRequest request = new WorkerRequest();
        request.setName(nombre);
        request.setSalary(salary);
        request.setNotas(notas);
        request.setPhoto(photoString);

        String token = (String) session.getAttribute("JWT_TOKEN");
        Response response = gatewayClient.post(
                "/api/workers/register",
                request,
                Response.class,
                token);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    @ResponseBody
    public ResponseEntity<Response> deleteWorker(@RequestBody Map<String, Integer> body, HttpSession session) {
        try {
            List<String> modules = jwtHelper.getUserModules(session);
            if (!modules.contains("MODULE_TRABAJADORES")) {
                return ResponseEntity.status(403).build();
            }

            String token = (String) session.getAttribute("JWT_TOKEN");
            Integer workerId = body.get("workerId");
            log.info("Intentando eliminar worker con ID: {}", workerId);

            if (workerId == null) {
                throw new IllegalArgumentException("ID del worker no proporcionado");
            }

            Map<String, Integer> requestBody = Map.of("workerId", workerId);

            Response response = gatewayClient.delete(
                    "/api/workers/delete",
                    requestBody,
                    Response.class,
                    token);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("[FAILED] - Error al eliminar worker: {}", e.getMessage());
            throw new RuntimeException("Error al eliminar worker: " + e.getMessage());
        }
    }

    @PostMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<Response> updateWorker(
            @RequestParam Integer workerId,
            @RequestParam String nombre,
            @RequestParam BigDecimal salary,
            @RequestParam(required = false) String notas,
            @RequestParam(required = false) MultipartFile photo,
            HttpSession session) {

        List<String> modules = jwtHelper.getUserModules(session);
        if (!modules.contains("MODULE_TRABAJADORES"))
            return ResponseEntity.status(403).build();

        // Convertir MultipartFile a String
        String photoString = null;

        log.info("[REQUEST UPDATE] - workerId: {}, nombre: {}, salary: {}, notas: {}, photo: {}",
                workerId, nombre, salary, notas, photo);

        if (photo != null && !photo.isEmpty()) {
            try {
                byte[] photoBytes = photo.getBytes();
                photoString = java.util.Base64.getEncoder().encodeToString(photoBytes);
            } catch (IOException e) {
                log.error("Error al convertir la foto: {}", e.getMessage());
                throw new RuntimeException("Error al convertir la foto: " + e.getMessage());
            }
        }

        WorkerRequest request = new WorkerRequest();
        request.setName(nombre);
        request.setSalary(salary);
        request.setNotas(notas);
        request.setPhoto(photoString);

        Response response = gatewayClient.put(
                "/api/workers/update/" + workerId,
                request,
                Response.class,
                session.getAttribute("JWT_TOKEN").toString());

        return ResponseEntity.ok(response);
    }
}