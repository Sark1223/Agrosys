package com.agrosys.transaction.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.agrosys.transaction.dto.CreatePaymentRequest;
import com.agrosys.transaction.dto.CreatePaymentRequest.PaymentItem;
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
        LocalDate endDate = request.getEndDate();
        String weekId = request.getWeekId();
        List<PaymentItem> payments = request.getPayments();

        if (endDate == null || weekId == null || payments == null || payments.isEmpty()) {
            throw new IllegalArgumentException("endDate, weekId y payments son requeridos");
        }

        List<AgrosysTransaction> created = new ArrayList<>();

        for (PaymentItem item : payments) {
            AgrosysTransaction tx = new AgrosysTransaction();
            tx.setTransactionType(AgrosysTransactionType.EXPENSE);
            tx.setCreateAt(endDate);
            tx.setAmount(item.getTotal());
            tx.setDescription("Pago semanal - " + item.getWorkerName() + " - " + weekId);
            tx.setPlotId(null);
            tx.setLotTradeId(null);
            created.add(transactionRepository.save(tx));
        }

        log.info("Pagos generados exitosamente para la semana {}: {} transacciones", weekId, created.size());

        return Response.builder()
                .success(true)
                .message("Pagos generados exitosamente (" + created.size() + " transacciones)")
                .data(null)
                .build();
    }
}
