package com.agrosys.worker.controller;

import java.math.BigDecimal;
import java.util.List;

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
    public ResponseEntity<Response> getWeeks() {
        List<Week> weeks = weeklyPayService.getAllWeeks();
        return ResponseEntity.ok(Response.builder()
                .success(true)
                .message("Semanas obtenidas exitosamente")
                .data(weeks)
                .build());
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('MODULE_FINANZAS')")
    public ResponseEntity<Response> getSummary(@RequestParam String weekId) {
        List<WeeklyPayRepository.WorkerPaymentProjection> summary =
                weeklyPayService.getPaymentSummary(weekId);
        return ResponseEntity.ok(Response.builder()
                .success(true)
                .message("Resumen obtenido exitosamente")
                .data(summary)
                .build());
    }

    @GetMapping("/amount")
    @PreAuthorize("hasAuthority('MODULE_FINANZAS')")
    public ResponseEntity<Response> getAmount(
            @RequestParam Integer workerId,
            @RequestParam String weekId) {
        BigDecimal amount = weeklyPayService.getWeeklyPayAmount(workerId, weekId);
        return ResponseEntity.ok(Response.builder()
                .success(true)
                .message("Monto obtenido exitosamente")
                .data(amount)
                .build());
    }

    @PostMapping("/upsert")
    @PreAuthorize("hasAuthority('MODULE_FINANZAS')")
    public ResponseEntity<Response> upsert( @RequestBody UpsertRequest request) {
        Response response = weeklyPayService.upsertWeeklyPay(
                request.getWorkerId(), request.getWeekId(), request.getAmount());
        return ResponseEntity.ok(response);
    }

    @lombok.Data
    public static class UpsertRequest {
        private Integer workerId;
        private String weekId;
        private BigDecimal amount;
    }
}
