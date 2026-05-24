package com.agrosys.transaction.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agrosys.transaction.dto.CreatePaymentRequest;
import com.agrosys.transaction.dto.Response;
import com.agrosys.transaction.service.WeeklyPayTransactionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/transaction")
@RequiredArgsConstructor
@Slf4j
public class AgrosysTransactionController {

    private final WeeklyPayTransactionService weeklyPayTransactionService;

    @PostMapping("/weekly-pay/create")
    @PreAuthorize("hasAuthority('MODULE_FINANZAS')")
    public ResponseEntity<Response> createPayments(@RequestBody CreatePaymentRequest request) {
        log.info("[REQUEST] - Crear pagos semanales: weekId={}", request.getWeekId());
        Response response = weeklyPayTransactionService.createPayments(request);
        return ResponseEntity.ok(response);
    }
}
