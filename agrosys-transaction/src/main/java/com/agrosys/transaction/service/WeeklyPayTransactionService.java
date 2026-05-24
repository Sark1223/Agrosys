package com.agrosys.transaction.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.agrosys.transaction.dto.CreatePaymentRequest;
import com.agrosys.transaction.dto.PaymentItem;
import com.agrosys.transaction.dto.Response;
import com.agrosys.transaction.entity.AgrosysTransaction;
import com.agrosys.transaction.entity.AgrosysTransactionType;
import com.agrosys.transaction.repository.TransactionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeeklyPayTransactionService {

    private final TransactionRepository transactionRepository;

    @Transactional
    public Response createPayments(CreatePaymentRequest request) {
        List<PaymentItem> payments = request.getPayments();
        if (payments == null || payments.isEmpty()) {
            throw new IllegalArgumentException("No hay pagos para registrar");
        }

        int count = 0;
        for (PaymentItem item : payments) {
            if (item.getAmount() == null || item.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                continue;
            }

            AgrosysTransaction transaction = new AgrosysTransaction();
            transaction.setTransactionType(AgrosysTransactionType.EXPENSE);
            transaction.setCreateAt(java.time.LocalDate.now());
            transaction.setAmount(item.getAmount());
            transaction.setDescription("Pago semanal trabajador #" + item.getWorkerId()
                    + " - Semana " + request.getWeekId());
            transactionRepository.save(transaction);
            count++;
        }

        log.info("{} pagos semanales registrados para la semana {}", count, request.getWeekId());
        return Response.builder()
                .success(true)
                .message(count + " pago(s) registrado(s) exitosamente")
                .build();
    }
}
