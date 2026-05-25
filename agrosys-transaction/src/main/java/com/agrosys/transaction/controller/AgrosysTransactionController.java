package com.agrosys.transaction.controller;

import java.time.LocalDate;
import java.util.List;

import org.apache.catalina.startup.ClassLoaderFactory.Repository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.agrosys.transaction.dto.CreatePaymentRequest;
import com.agrosys.transaction.dto.Response;
import com.agrosys.transaction.dto.TransactionRequest;
import com.agrosys.transaction.entity.AgrosysTransaction;
import com.agrosys.transaction.repository.TransactionRepository;
import com.agrosys.transaction.repository.TransactionRepository.TransactionTotalsProjection;
import com.agrosys.transaction.service.TransactionService;
import com.agrosys.transaction.service.WeeklyPayTransactionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/transaction")
@RequiredArgsConstructor
@Slf4j
public class AgrosysTransactionController {

    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;
    private final WeeklyPayTransactionService weeklyPayTransactionService;

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('MODULE_FINANZAS')")
    public ResponseEntity<Response> getAll(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer plotId) {
        List<TransactionRepository.TransactionProjection> transactions = transactionService.getAllWithPlot(plotId,
                startDate, endDate, type);
        return ResponseEntity.ok(Response.builder()
                .success(true)
                .message("Transacciones obtenidas exitosamente")
                .data(transactions)
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('MODULE_FINANZAS')")
    public ResponseEntity<Response> getById(@PathVariable Integer id) {
        AgrosysTransaction transaction = transactionService.getById(id);
        return ResponseEntity.ok(Response.builder()
                .success(true)
                .message("Transacci\u00f3n obtenida exitosamente")
                .data(transaction)
                .build());
    }

    @PostMapping("/register")
    @PreAuthorize("hasAuthority('MODULE_FINANZAS')")
    public ResponseEntity<Response> create(@Valid @RequestBody TransactionRequest request) {
        log.info("[REQUEST] - Crear transacci\u00f3n: {}", request);
        Response response = transactionService.create(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('MODULE_FINANZAS')")
    public ResponseEntity<Response> update(@PathVariable Integer id, @Valid @RequestBody TransactionRequest request) {
        log.info("[REQUEST] - Actualizar transacci\u00f3n {}: {}", id, request);
        Response response = transactionService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('MODULE_FINANZAS')")
    public ResponseEntity<Response> delete(@PathVariable Integer id) {
        log.info("[REQUEST] - Eliminar transacci\u00f3n: {}", id);
        Response response = transactionService.delete(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register-payments")
    @PreAuthorize("hasAuthority('MODULE_FINANZAS')")
    public ResponseEntity<Response> registerPayments(@Valid @RequestBody CreatePaymentRequest request) {
        log.info("[REQUEST] - Registrar pagos semanales: {} pagos para semana {}",
                request.getPayments() != null ? request.getPayments().size() : 0,
                request.getWeekId());
        Response response = weeklyPayTransactionService.createPayments(request);
        return ResponseEntity.ok(response);
    }

    /* CONTENEDORES DE INGRESOS Y GASTOS */
    @GetMapping("/totals") 
    @PreAuthorize("hasAuthority('MODULE_FINANZAS')")
    public ResponseEntity<Response> transaction(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        TransactionTotalsProjection totals = transactionRepository.getTransactionTotals(startDate, endDate);

        return ResponseEntity.ok(Response.builder()
                .success(true)
                .message("Transacciones obtenidas exitosamente")
                .data(totals)
                .build());
    }

}
