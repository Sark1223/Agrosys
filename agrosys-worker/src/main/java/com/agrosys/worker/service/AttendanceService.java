package com.agrosys.worker.service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.agrosys.worker.repository.WorkerRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceService {

    // Inyectamos el repositorio que acabas de modificar
    private final WorkerRepository workerRepository;

    @Transactional
    public void registrarAsistenciaSemanal(List<Map<String, Object>> registros) {
        log.info("[ATTENDANCE] - Recibiendo {} registros de asistencia", registros.size());

        for (Map<String, Object> registro : registros) {
            Integer workerId = (Integer) registro.get("workerId");
            LocalDate date = LocalDate.parse((String) registro.get("date"));
            Boolean attended = (Boolean) registro.get("attended");
            Integer hoursWorked = (Integer) registro.get("hoursWorked");

            // Validar que no exista ya un registro para ese trabajador en ese día
            if (workerRepository.existsAttendanceByWorkerAndDate(workerId, date) > 0) {
                log.warn("[ATTENDANCE] - El trabajador {} ya tiene asistencia el {}", workerId, date);
                throw new RuntimeException(
                        "El trabajador con ID " + workerId + " ya tiene un registro para la fecha " + date);
            }

            workerRepository.insertAttendance(workerId, date, attended, hoursWorked);
        }

        log.info("[ATTENDANCE SUCCESS] - Asistencia guardada correctamente");
    }

    public List<WorkerRepository.AttendanceHistoryProjection> consultarHistorial(Integer workerId, String startDate,
            String endDate) {
        log.info("[ATTENDANCE HISTORY] - Consultando historial simple");
        return workerRepository.getAttendanceHistory(workerId, startDate, endDate);
    }

    // ================= REPORTE SEMANAL DE SALARIOS ===================
    public Map<String, Object> getWeeklySalaryReport() {
        log.info("[WEEKLY SALARY] - Generando reporte semanal de salarios");

        // Calcular lunes y viernes de la semana actual
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(4);
        
        List<WorkerRepository.WeeklySalaryProjection> rows = workerRepository.getWeeklySalaryReport(weekStart, weekEnd);

        // Días de la semana en orden
        List<String> dayKeys = List.of("lunes", "martes", "miercoles", "jueves", "viernes");
        Map<String, LocalDate> dayDates = Map.of(
                "lunes", weekStart,
                "martes", weekStart.plusDays(1),
                "miercoles", weekStart.plusDays(2),
                "jueves", weekStart.plusDays(3),
                "viernes", weekStart.plusDays(4));

        // Agrupar filas por trabajador
        Map<Integer, Map<String, Object>> workerMap = new LinkedHashMap<>();

        for (WorkerRepository.WeeklySalaryProjection row : rows) {
            Integer workerId = row.getWorkerId();

            // Inicializar trabajador si es la primera vez
            if (!workerMap.containsKey(workerId)) {
                Map<String, Object> workerData = new LinkedHashMap<>();
                workerData.put("workerId", workerId);
                workerData.put("name", row.getName());
                for (String day : dayKeys) {
                    workerData.put(day, null);
                }
                workerData.put("extras", BigDecimal.ZERO);
                workerData.put("total", BigDecimal.ZERO);
                workerMap.put(workerId, workerData);
            }

            Map<String, Object> workerData = workerMap.get(workerId);

            // Si tiene fecha de asistencia, calcular salario del día
            if (row.getDate() != null && row.getHoursWorked() != null) {
                LocalDate attendanceDate = row.getDate();
                BigDecimal dailySalary = row.getHourlyPay()
                        .multiply(BigDecimal.valueOf(row.getHoursWorked()));

                // Asignar al día correspondiente
                for (Map.Entry<String, LocalDate> entry : dayDates.entrySet()) {
                    if (entry.getValue().equals(attendanceDate)) {
                        // Solo mostrar si el día ya pasó o es hoy
                        if (!attendanceDate.isAfter(today)) {
                            workerData.put(entry.getKey(), dailySalary);
                        }
                        break;
                    }
                }
            }

            BigDecimal extras = row.getExtras() != null ? row.getExtras() : BigDecimal.ZERO;
            if (extras.compareTo((BigDecimal) workerData.get("extras")) > 0) {
                workerData.put("extras", extras);
            }
        }

        // Calcular totales por trabajador y total general
        BigDecimal grandTotal = BigDecimal.ZERO;
        List<Map<String, Object>> workerList = new ArrayList<>();

        for (Map<String, Object> workerData : workerMap.values()) {
            BigDecimal total = BigDecimal.ZERO;
            for (String day : dayKeys) {
                BigDecimal daySalary = (BigDecimal) workerData.get(day);
                if (daySalary != null) {
                    total = total.add(daySalary);
                }
            }
            total = total.add((BigDecimal) workerData.get("extras"));
            workerData.put("total", total);
            grandTotal = grandTotal.add(total);
            workerList.add(workerData);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("weekStart", weekStart.toString());
        result.put("weekEnd", weekEnd.toString());
        result.put("generatedDay", today.getDayOfWeek().toString().toLowerCase());
        result.put("workers", workerList);
        result.put("grandTotal", grandTotal);

        log.info("[WEEKLY SALARY SUCCESS] - Reporte generado para {} trabajadores", workerList.size());
        return result;
    }
}