package com.agrosys.transaction.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.agrosys.transaction.dto.Response;
import com.agrosys.transaction.dto.TransactionRequest;
import com.agrosys.transaction.entity.AgrosysTransaction;
import com.agrosys.transaction.entity.AgrosysTransactionType;
import com.agrosys.transaction.repository.TransactionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public List<AgrosysTransaction> getAll(
            LocalDate startDate, LocalDate endDate, String type
    ) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin son obligatorias");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("La fecha de incio no puede ser matoy a la fecha de fin");
        }
        log.info("Obteniendo transacciones desde {} hasta {} tipo {}", startDate, endDate, type);
        return transactionRepository.findByFilters(startDate, endDate, type);
    }

    public AgrosysTransaction getById(Integer id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transacción no encontrada"));
    }

    @Transactional
    public Response create(TransactionRequest request) {
        AgrosysTransaction transaction = new AgrosysTransaction();
        transaction.setTransactionType(AgrosysTransactionType.valueOf(request.getTransactionType()));
        transaction.setCreateAt(request.getCreateAt());
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setPlotId(request.getPlotId());
        transaction.setLotTradeId(request.getLotTradeId());
        transactionRepository.save(transaction);
        log.info("Transaccion creada: {}", transaction);

        return Response.builder().success(true).message("Transaccion registrada exitosamente").build();
    }

    @Transactional
    public Response update(Integer id, TransactionRequest request) {
        AgrosysTransaction transaction = getById(id);
        transaction.setTransactionType(AgrosysTransactionType.valueOf(request.getTransactionType()));
        transaction.setCreateAt(request.getCreateAt());
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setPlotId(request.getPlotId());
        transaction.setLotTradeId(request.getLotTradeId());
        transactionRepository.save(transaction);
        log.info("Transacción actualizada: {}", transaction);
        return Response.builder()
                .success(true)
                .message("Transacción actualizada exitosamente")
                .build();
    }

    @Transactional
    public Response delete(Integer id) {
        AgrosysTransaction transaction = getById(id);
        transactionRepository.delete(transaction);
        log.info("Transacción eliminada: {}", id);
        return Response.builder()
                .success(true)
                .message("Transacción eliminada exitosamente")
                .build();
    }

    public List<TransactionRepository.TransactionProjection> getAllWithPlot(
            Integer plotId, LocalDate startDate, LocalDate endDate, String type
    ) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin son obligatorias");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser mayor a la fecha de fin");
        }
        log.info("Obteniendo transacciones con filtros - plot; {}, desde: {}, hasta: {}, tipo: {}",
                plotId, startDate, endDate, type);

        return transactionRepository.findByFiltersWithPlot(plotId, startDate, endDate, type);
    }
}
