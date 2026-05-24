package com.agrosys.worker.service;

import java.time.LocalDate;
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
                throw new RuntimeException("El trabajador con ID " + workerId + " ya tiene un registro para la fecha " + date);
            }

            workerRepository.insertAttendance(workerId, date, attended, hoursWorked);
        }
        
        log.info("[ATTENDANCE SUCCESS] - Asistencia guardada correctamente");
    }

    public List<WorkerRepository.AttendanceHistoryProjection> consultarHistorial(Integer workerId, String startDate, String endDate) {
        log.info("[ATTENDANCE HISTORY] - Consultando historial simple");
        return workerRepository.getAttendanceHistory(workerId, startDate, endDate);
    }
}