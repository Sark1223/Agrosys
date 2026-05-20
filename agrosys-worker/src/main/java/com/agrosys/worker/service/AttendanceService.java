package com.agrosys.worker.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.agrosys.worker.dto.AttendanceRequest;
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
    public void registrarAsistenciaSemanal(List<AttendanceRequest> registros) {
        log.info("[ATTENDANCE] - Procesando {} registros", registros.size());

        for (AttendanceRequest registro : registros) {
            // Ya no usamos Map, ahora usamos los getters del DTO
            Integer workerId = registro.getWorkerId();
            // Asegúrate de que el DTO maneja LocalDate. Si el JSON envía un String, 
            // y tu DTO tiene LocalDate, Jackson lo convertirá automáticamente si tiene el formato correcto.
            LocalDate date = registro.getDate(); 
            Boolean attended = registro.getAttended();
            Integer hoursWorked = registro.getHoursWorked();

            if (workerRepository.existsAttendanceByWorkerAndDate(workerId, date) > 0) {
                log.info("[ATTENDANCE UPDATE] - Actualizando asistencia para worker {} en fecha {}", workerId, date);
                workerRepository.updateAttendance(workerId, date, attended, hoursWorked);
            } else {
                log.info("[ATTENDANCE INSERT] - Nuevo registro para worker {} en fecha {}", workerId, date);
                workerRepository.insertAttendance(workerId, date, attended, hoursWorked);
            }
        }
    }

    public List<WorkerRepository.AttendanceHistoryProjection> consultarHistorial(Integer workerId, String startDate, String endDate) {
        log.info("[ATTENDANCE HISTORY] - Consultando historial simple");
        return workerRepository.getAttendanceHistory(workerId, startDate, endDate);
    }

    public boolean verificarAsistenciaGuardada(LocalDate date) {
        log.info("[ATTENDANCE CHECK] - Verificando si existe asistencia para la fecha: {}", date);
        return workerRepository.countAttendanceByDate(date) > 0;
    }
    
}