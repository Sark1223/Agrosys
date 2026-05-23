package com.agrosys.transaction.controller;

import com.agrosys.transaction.dto.Response;
import com.agrosys.transaction.dto.TransactionRequest;
import com.agrosys.transaction.entity.AgrosysTransaction;
import com.agrosys.transaction.repository.TransactionRepository;
import com.agrosys.transaction.service.TransactionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transaction")
@RequiredArgsConstructor
@Slf4j
public class AgrosysTransactionController {

    private final TransactionService transactionService;

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('MODULE_FINANZAS')")
    public ResponseEntity<Response> getAll(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer plotId) {
        List<TransactionRepository.TransactionProjection> transactions =
                transactionService.getAllWithPlot(plotId, startDate, endDate, type);
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
                .message("Transacción obtenida exitosamente")
                .data(transaction)
                .build());
    }

    @PostMapping("/register")
    @PreAuthorize("hasAuthority('MODULE_FINANZAS')")
    public ResponseEntity<Response> create(@Valid @RequestBody TransactionRequest request) {
        log.info("[REQUEST] - Crear transacción: {}", request);
        Response response = transactionService.create(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('MODULE_FINANZAS')")
    public ResponseEntity<Response> update(@PathVariable Integer id, @Valid @RequestBody TransactionRequest request) {
        log.info("[REQUEST] - Actualizar transacción {}: {}", id, request);
        Response response = transactionService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('MODULE_FINANZAS')")
    public ResponseEntity<Response> delete(@PathVariable Integer id) {
        log.info("[REQUEST] - Eliminar transacción: {}", id);
        Response response = transactionService.delete(id);
        return ResponseEntity.ok(response);
    }
}
