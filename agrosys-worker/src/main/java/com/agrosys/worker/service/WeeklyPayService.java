package com.agrosys.worker.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.agrosys.worker.dto.Response;
import com.agrosys.worker.entity.Week;
import com.agrosys.worker.repository.WeekRepository;
import com.agrosys.worker.repository.WeeklyPayRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeeklyPayService {

    private final WeekRepository weekRepository;
    private final WeeklyPayRepository weeklyPayRepository;

    public List<Week> getAllWeeks() {
        log.info("Obteniendo todas las semanas");
        return weekRepository.findAllByOrderByStartDateAsc();
    }

    public List<WeeklyPayRepository.WorkerPaymentProjection> getPaymentSummary(String weekId) {
        log.info("Obteniendo resumen de pagos para la semana {}", weekId);
        return weeklyPayRepository.findPaymentSummary(weekId);
    }

    public BigDecimal getWeeklyPayAmount(Integer workerId, String weekId) {
        log.info("Obteniendo pago semanal para worker {} en semana {}", workerId, weekId);
        return weeklyPayRepository.findAmountByWorkerIdAndWeekId(workerId, weekId).orElse(null);
    }

    @Transactional
    public Response upsertWeeklyPay(Integer workerId, String weekId, BigDecimal amount) {
        log.info("Guardando pago semanal - worker: {}, week: {}, amount: {}", workerId, weekId, amount);
        weeklyPayRepository.upsertWeeklyPay(workerId, weekId, amount);
        return Response.builder()
                .success(true)
                .message("Pago semanal guardado exitosamente")
                .build();
    }
}
