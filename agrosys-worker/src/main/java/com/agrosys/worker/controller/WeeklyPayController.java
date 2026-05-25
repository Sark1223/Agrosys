package com.agrosys.worker.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.agrosys.worker.dto.Response;
import com.agrosys.worker.entity.Week;
import com.agrosys.worker.repository.WeeklyPayRepository;
import com.agrosys.worker.service.WeeklyPayService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/workers/weekly-pay")
@RequiredArgsConstructor
@Slf4j
public class WeeklyPayController {

    private final WeeklyPayService weeklyPayService;

    @GetMapping("/weeks")
    @PreAuthorize("hasAuthority('MODULE_FINANZAS')")
    public ResponseEntity<Response> getAllWeeks() {
        List<Week> weeks = weeklyPayService.getAllWeeks();
        return ResponseEntity.ok(Response.builder()
                .success(true)
                .message("Semanas obtenidas exitosamente")
                .data(weeks)
                .build());
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('MODULE_FINANZAS')")
    public ResponseEntity<Response> getPaymentSummary(@RequestParam Integer weekId) {
        List<WeeklyPayRepository.PaymentSummaryProjection> summary =
                weeklyPayService.getPaymentSummary(weekId);
        return ResponseEntity.ok(Response.builder()
                .success(true)
                .message("Resumen de pagos obtenido exitosamente")
                .data(summary)
                .build());
    }

    @GetMapping("/amount")
    @PreAuthorize("hasAuthority('MODULE_FINANZAS')")
    public ResponseEntity<Response> getAmount(
            @RequestParam Integer workerId,
            @RequestParam Integer weekId) {
        BigDecimal amount = weeklyPayService.getWeeklyPayAmount(workerId, weekId);
        return ResponseEntity.ok(Response.builder()
                .success(true)
                .message("Monto obtenido exitosamente")
                .data(Map.of("amount", amount != null ? amount : BigDecimal.ZERO))
                .build());
    }

    @PostMapping("/upsert")
    @PreAuthorize("hasAuthority('MODULE_FINANZAS')")
    public ResponseEntity<Response> upsertWeeklyPay(@RequestBody Map<String, Object> request) {
        Integer workerId = Integer.valueOf(request.get("workerId").toString());
        Integer weekId = Integer.valueOf(request.get("weekId").toString());
        BigDecimal amount = new BigDecimal(request.get("amount").toString());

        weeklyPayService.upsertWeeklyPay(workerId, weekId, amount);
        return ResponseEntity.ok(Response.builder()
                .success(true)
                .message("Pago semanal actualizado exitosamente")
                .build());
    }
}
