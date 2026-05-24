package com.agrosys.worker.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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

    interface WorkerPaymentProjection {
        Integer getWorkerId();
        String getWorkerName();
        BigDecimal getWeeklyPay();
        BigDecimal getExtras();
        BigDecimal getTotal();
    }

    @Query(value = """
        SELECT w.workerId, w.name AS workerName,
               COALESCE(wp.amount, ROUND(w.salary / 52, 2)) AS weeklyPay,
               COALESCE(SUM(st.payment_amount), 0) AS extras,
               COALESCE(wp.amount, ROUND(w.salary / 52, 2)) + COALESCE(SUM(st.payment_amount), 0) AS total
        FROM agrosys_worker.WORKER w
        JOIN agrosys_worker.week wk ON wk.week_id = :weekId
        LEFT JOIN agrosys_worker.weekly_pay wp ON wp.worker_id = w.workerId AND wp.week_id = wk.week_id
        LEFT JOIN agrosys_task.WORKER_TASK wt ON wt.worker_id = w.workerId
        LEFT JOIN agrosys_task.SPECIAL_TASK st ON st.special_task_id = wt.special_task_id
            AND (st.create_at BETWEEN wk.start_date AND wk.end_date
                 OR st.end_at BETWEEN wk.start_date AND wk.end_date)
        GROUP BY w.workerId, w.name, w.salary, wp.amount
        ORDER BY w.name
        """, nativeQuery = true)
    List<WorkerPaymentProjection> findPaymentSummary(@Param("weekId") String weekId);

    @Query(value = """
        SELECT wp.amount
        FROM agrosys_worker.weekly_pay wp
        WHERE wp.worker_id = :workerId AND wp.week_id = :weekId
        """, nativeQuery = true)
    Optional<BigDecimal> findAmountByWorkerIdAndWeekId(@Param("workerId") Integer workerId, @Param("weekId") String weekId);

    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO agrosys_worker.weekly_pay (worker_id, week_id, amount)
        VALUES (:workerId, :weekId, :amount)
        ON DUPLICATE KEY UPDATE amount = :amount
        """, nativeQuery = true)
    int upsertWeeklyPay(@Param("workerId") Integer workerId, @Param("weekId") String weekId, @Param("amount") BigDecimal amount);
}
