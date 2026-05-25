package com.agrosys.transaction.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.agrosys.transaction.entity.AgrosysTransaction;

@Repository
public interface TransactionRepository extends JpaRepository<AgrosysTransaction, Integer> {

    interface TransactionTotalsProjection {
        Double getTotalIngresos();
        Double getTotalEgresos();
    }

    interface TransactionProjection {
        Integer getTransactionId();

        String getTransactionType();

        LocalDate getCreateAt();

        BigDecimal getAmount();

        String getDescription();

        Integer getPlotId();

        String getPlotName();

        Integer getLotTradeId();
    }

    @Query(value = """
            SELECT t.transaction_id, t.transaction_type, t.create_at,
                   t.amount, t.description, t.lot_trade_id,
                   COALESCE(pl.plotId, pl_direct.plotId, t.plot_id) AS plot_id,
                   COALESCE(pl.name, pl_direct.name) AS plot_name
            FROM agrosys_db.transactions t
            LEFT JOIN agrosys_db.LOT_TRADE lt ON lt.lotTradeId = t.lot_trade_id
            LEFT JOIN agrosys_db.LOT l ON l.lotId = lt.loteId
            LEFT JOIN agrosys_db.plantatio p ON p.plantatioId = l.plantationId
            LEFT JOIN agrosys_db.plot pl ON pl.plotId = p.plotId
            LEFT JOIN agrosys_db.plot pl_direct ON pl_direct.plotId = t.plot_id
            WHERE t.create_at BETWEEN :startDate AND :endDate
              AND (:type IS NULL OR t.transaction_type = :type)
              AND (:plotId IS NULL OR COALESCE(pl.plotId, pl_direct.plotId, t.plot_id) = :plotId)
            ORDER BY t.create_at DESC, t.transaction_id DESC
            """, nativeQuery = true)
    List<TransactionProjection> findByFiltersWithPlot(Integer plotId, LocalDate startDate, LocalDate endDate,
            String type);

    @Query(value = """
            SELECT * FROM agrosys_db.transactions t
            WHERE t.create_at BETWEEN :startDate AND :endDate
              AND (:type IS NULL OR t.transaction_type = :type)
            ORDER BY t.create_at DESC, t.transaction_id DESC
            """, nativeQuery = true)
    List<AgrosysTransaction> findByFilters(LocalDate startDate, LocalDate endDate, String type);

    /* CONTENEDORES DE INGRESOS Y GASTOS */
    @Query(value = """
            SELECT
                SUM(CASE WHEN transaction_type = 'INCOME' THEN amount ELSE 0 END) AS totalIngresos,
                SUM(CASE WHEN transaction_type = 'EXPENSE' THEN amount ELSE 0 END) AS totalEgresos
            FROM agrosys_db.transactions
            WHERE DATE(create_at) BETWEEN :startDate AND :endDate
                                    """, nativeQuery = true)
    TransactionTotalsProjection getTransactionTotals(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}
