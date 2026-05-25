package com.agrosys.worker.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

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
        return weekRepository.findAllByOrderByStartDateAsc();
    }

    public List<WeeklyPayRepository.PaymentSummaryProjection> getPaymentSummary(Integer weekId) {
        return weeklyPayRepository.findPaymentSummaryByWeekId(weekId);
    }

    public BigDecimal getWeeklyPayAmount(Integer workerId, Integer weekId) {
        return weeklyPayRepository.findAmountByWorkerIdAndWeekId(workerId, weekId);
    }

    @Transactional
    public void upsertWeeklyPay(Integer workerId, Integer weekId, BigDecimal amount) {
        weeklyPayRepository.upsertWeeklyPay(workerId, weekId, amount);
        log.info("Weekly pay upserted - worker: {}, week: {}, amount: {}", workerId, weekId, amount);
    }
}
