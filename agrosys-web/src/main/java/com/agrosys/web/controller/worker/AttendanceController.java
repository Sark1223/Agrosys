package com.agrosys.web.controller.worker;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.agrosys.web.dto.Response;
import com.agrosys.web.utils.GatewayClient;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/workers/attendance")
@RequiredArgsConstructor
@Slf4j
public class AttendanceController {

    private final GatewayClient gatewayClient;

    @GetMapping("/get-active-workers")
    @ResponseBody
    public ResponseEntity<Response> getActiveWorkers(HttpSession session) {
        try {
            String token = (String) session.getAttribute("JWT_TOKEN");
            Response response = gatewayClient.get("/api/workers/attendance/active-workers", Response.class, token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("[FAILED] - Error al obtener trabajadores para asistencia: {}", e.getMessage());
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    // Guardar la asistencia
    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<Response> registerAttendance(@RequestBody List<Map<String, Object>> body,
            HttpSession session) {
        try {
            String token = (String) session.getAttribute("JWT_TOKEN");
            Response response = gatewayClient.post("/api/workers/attendance/register-bulk", body, Response.class,
                    token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("[FAILED] - Error al registrar asistencia: {}", e.getMessage());
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @GetMapping("/history")
    @ResponseBody
    public ResponseEntity<Response> getAttendanceHistory(
            @RequestParam(required = false) Integer workerId,
            @RequestParam String startDate,
            @RequestParam String endDate,
            HttpSession session) {
        try {
            String token = (String) session.getAttribute("JWT_TOKEN");

            // Armamos la URL con los filtros para el Gateway
            String url = "/api/workers/attendance/history?";
            if (workerId != null)
                url += "workerId=" + workerId + "&";
            
            url += "startDate=" + startDate + "&endDate=" + endDate;

            Response response = gatewayClient.get(url, Response.class, token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("[FAILED] - Error al obtener historial: {}", e.getMessage());
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @GetMapping("/check")
    @ResponseBody
    public ResponseEntity<Response> checkAttendance(@RequestParam String date, HttpSession session) {
        try {
            String token = (String) session.getAttribute("JWT_TOKEN");
            // El puente hacia el nuevo endpoint del worker
            String url = "/api/workers/attendance/check?date=" + date;
            Response response = gatewayClient.get(url, Response.class, token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("[FAILED] - Error al verificar asistencia: {}", e.getMessage());
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}