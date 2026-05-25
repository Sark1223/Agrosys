package com.agrosys.worker.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.agrosys.worker.entity.WeeklyPay;
import com.agrosys.worker.entity.WeeklyPayId;

import jakarta.transaction.Transactional;

@Repository
public interface WeeklyPayRepository extends JpaRepository<WeeklyPay, WeeklyPayId> {

    interface PaymentSummaryProjection {
        Integer getWorkerId();
        String getWorkerName();
        BigDecimal getSalary();
        BigDecimal getAmount();
        BigDecimal getBonus();
    }

    @Query(value = """
        SELECT
            w.workerId AS workerId,
            w.name AS workerName,
            w.salary AS salary,
            wp.amount AS amount,
            COALESCE(st_bonus.total_bonus, 0) AS bonus
        FROM agrosys_worker.WORKER w
        LEFT JOIN agrosys_worker.weekly_pay wp
            ON wp.worker_id = w.workerId AND wp.week_id = :weekId
        LEFT JOIN (
            SELECT wt.worker_id, SUM(st.payment_amount) AS total_bonus
            FROM agrosys_task.SPECIAL_TASK st
            INNER JOIN agrosys_task.WORKER_TASK wt ON st.special_task_id = wt.special_task_id
            INNER JOIN agrosys_worker.week wk ON st.create_at BETWEEN wk.start_date AND wk.end_date
            WHERE wk.week_id = :weekId
            GROUP BY wt.worker_id
        ) st_bonus ON st_bonus.worker_id = w.workerId
        ORDER BY w.name ASC
        """, nativeQuery = true)
    List<PaymentSummaryProjection> findPaymentSummaryByWeekId(@Param("weekId") Integer weekId);

    @Query(value = """
        SELECT amount
        FROM agrosys_worker.weekly_pay
        WHERE worker_id = :workerId AND week_id = :weekId
        """, nativeQuery = true)
    BigDecimal findAmountByWorkerIdAndWeekId(@Param("workerId") Integer workerId, @Param("weekId") Integer weekId);

    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO agrosys_worker.weekly_pay (worker_id, week_id, amount)
        VALUES (:workerId, :weekId, :amount)
        ON DUPLICATE KEY UPDATE amount = :amount
        """, nativeQuery = true)
    int upsertWeeklyPay(@Param("workerId") Integer workerId, @Param("weekId") Integer weekId, @Param("amount") BigDecimal amount);
}
