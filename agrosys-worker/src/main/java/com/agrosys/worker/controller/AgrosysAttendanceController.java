package com.agrosys.worker.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.agrosys.worker.dto.Response;
import com.agrosys.worker.repository.WorkerRepository;
import com.agrosys.worker.service.AttendanceService; // <-- Importamos el servicio

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/workers/attendance") // <-- ruta base
@RequiredArgsConstructor
@Slf4j
public class AgrosysAttendanceController {

    // Ya descomentamos el servicio para poder usarlo
    private final AttendanceService attendanceService;

    // 1. Endpoint para obtener la lista de trabajadores (lo puedes dejar así por ahora si ya los cargas de otro lado)
    @GetMapping("/active-workers")
    @PreAuthorize("hasAuthority('MODULE_TRABAJADORES')")
    public ResponseEntity<Response> getActiveWorkersForAttendance() {
        log.info("[REQUEST] - Obteniendo trabajadores para registro de asistencia");
        
        Response response = Response.builder()
                .success(true)
                .message("Trabajadores obtenidos")
                .build();
        return ResponseEntity.ok(response);
    }

    // 2. Endpoint para guardar la lista de asistencias
    @PostMapping("/register-bulk")
    @PreAuthorize("hasAuthority('MODULE_TRABAJADORES')")
    // Le agregamos el @RequestBody para que atrape la lista que le manda el JavaScript
    public ResponseEntity<Response> registerBulkAttendance(@RequestBody List<Map<String, Object>> requests) { 
        log.info("[REQUEST] - Registrando asistencia masiva");

        try {
            // Le pasamos los datos al Chef (AttendanceService)
            attendanceService.registrarAsistenciaSemanal(requests);

            Response response = Response.builder()
                    .success(true)
                    .message("Asistencia guardada correctamente")
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            // Si el servicio detecta un duplicado, atrapamos el error y avisamos al frontend
            log.error("[FAILED] - Error al guardar la asistencia masiva: {}", e.getMessage());
            Response errorResponse = Response.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/history")
    @PreAuthorize("hasAuthority('MODULE_TRABAJADORES')")
    public ResponseEntity<Response> getAttendanceHistory(
            @RequestParam(required = false) Integer workerId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        try {
            List<WorkerRepository.AttendanceHistoryProjection> historial = attendanceService.consultarHistorial(workerId, startDate, endDate);
            
            Response response = Response.builder()
                    .success(true)
                    .message("Historial obtenido")
                    .data(historial)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                Response.builder().success(false).message(e.getMessage()).build()
            );
        }
    }
}