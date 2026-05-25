package com.agrosys.worker.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.agrosys.worker.dto.Response;
import com.agrosys.worker.repository.WorkerRepository;
import com.agrosys.worker.service.AttendanceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/workers/attendance")
@RequiredArgsConstructor
@Slf4j
public class AgrosysAttendanceController {

    private final AttendanceService attendanceService;
    private final WorkerRepository workerRepository;

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

    @PostMapping("/register-bulk")
    @PreAuthorize("hasAuthority('MODULE_TRABAJADORES')")
    public ResponseEntity<Response> registerBulkAttendance(@RequestBody List<Map<String, Object>> requests) {
        log.info("[REQUEST] - Registrando asistencia masiva");

        try {
            attendanceService.registrarAsistenciaSemanal(requests);

            Response response = Response.builder()
                    .success(true)
                    .message("Asistencia guardada correctamente")
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            log.error("[FAILED] - Error al guardar la asistencia masiva: {}", e.getMessage());

            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @GetMapping("/history")
    @PreAuthorize("hasAuthority('MODULE_TRABAJADORES')")
    public ResponseEntity<Response> getAttendanceHistory(
            @RequestParam(required = false) Integer workerId,
            @RequestParam String startDate,
            @RequestParam String endDate) {

        try {
            List<WorkerRepository.AttendanceHistoryProjection> historial = attendanceService
                    .consultarHistorial(workerId, startDate, endDate);

            Response response = Response.builder()
                    .success(true)
                    .message("Historial obtenido")
                    .data(historial)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @GetMapping("/asistence")
    @PreAuthorize("hasAuthority('MODULE_TRABAJADORES')")
    public ResponseEntity<Response> asistence() {
        try {
            int asistencia = workerRepository.asistencia();
            Response response = Response.builder()
                    .success(true)
                    .message("Asistencia obtenida")
                    .data(asistencia)
                    .build();
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    // ================= REPORTE SEMANAL DE SALARIOS ===================
    @GetMapping("/weekly-salary")
    @PreAuthorize("hasAuthority('MODULE_TRABAJADORES')")
    public ResponseEntity<Response> getWeeklySalaryReport() {
        log.info("[REQUEST] - Generando reporte semanal de salarios");
        try {
            Map<String, Object> report = attendanceService.getWeeklySalaryReport();
            Response response = Response.builder()
                    .success(true)
                    .message("Reporte semanal generado exitosamente")
                    .data(report)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("[FAILED] - Error al generar reporte semanal: {}", e.getMessage());
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

}