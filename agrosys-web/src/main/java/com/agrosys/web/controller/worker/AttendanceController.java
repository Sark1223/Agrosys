package com.agrosys.web.controller.worker;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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

    // Obtener trabajadores para la tabla de asistencia
    @GetMapping("/get-active-workers")
    @ResponseBody
    public ResponseEntity<Response> getActiveWorkers(HttpSession session) {
        try {
            String token = (String) session.getAttribute("JWT_TOKEN");
            // Llama al endpoint que creamos en el worker
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
    public ResponseEntity<Response> registerAttendance(@RequestBody List<Map<String, Object>> body, HttpSession session) {
        try {
            String token = (String) session.getAttribute("JWT_TOKEN");
            // Llama al endpoint de guardado masivo en el worker
            Response response = gatewayClient.post("/api/workers/attendance/register-bulk", body, Response.class, token);
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
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            HttpSession session) {
        try {
            String token = (String) session.getAttribute("JWT_TOKEN");
            
            // Armamos la URL con los filtros para el Gateway
            String url = "/api/workers/attendance/history?";
            if (workerId != null) url += "workerId=" + workerId + "&";
            if (startDate != null && !startDate.isEmpty()) url += "startDate=" + startDate + "&";
            if (endDate != null && !endDate.isEmpty()) url += "endDate=" + endDate;

            // El GatewayClient se encarga de ir al puerto 8085 por nosotros
            Response response = gatewayClient.get(url, Response.class, token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("[FAILED] - Error al obtener historial: {}", e.getMessage());
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}