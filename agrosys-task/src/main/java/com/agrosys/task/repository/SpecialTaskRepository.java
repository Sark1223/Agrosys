package com.agrosys.task.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.agrosys.task.entity.SpecialTask;

import jakarta.transaction.Transactional;

@Repository
public interface SpecialTaskRepository extends JpaRepository<SpecialTask, Integer> {

    interface SpecialTaskProjection {
        Integer getTaskId();
        Integer getWorkerId();
        java.math.BigDecimal getPaymentAmount();
        Integer getTaskStageId();
    }

    @Query(value = """
            SELECT
                st.task_id,
                st.worker_id,
                st.payment_amount,
                t.task_stage_id
            FROM agrosys_task.SPECIAL_TASK st
            INNER JOIN agrosys_task.TASK t ON st.task_id = t.task_id
            ORDER BY t.create_at DESC
            """, nativeQuery = true)
    List<SpecialTaskProjection> findAllSpecialTasks();

    @Query(value = """
            SELECT
                st.task_id,
                st.worker_id,
                st.payment_amount,
                t.task_stage_id
            FROM agrosys_task.SPECIAL_TASK st
            INNER JOIN agrosys_task.TASK t ON st.task_id = t.task_id
            WHERE st.worker_id = :workerId
            ORDER BY t.create_at DESC
            """, nativeQuery = true)
    List<SpecialTaskProjection> findByWorkerId(@Param("workerId") Integer workerId);

    @Query(value = """
            SELECT
                st.task_id,
                st.worker_id,
                st.payment_amount,
                t.task_stage_id
            FROM agrosys_task.SPECIAL_TASK st
            INNER JOIN agrosys_task.TASK t ON st.task_id = t.task_id
            WHERE st.task_id = :taskId
            """, nativeQuery = true)
    SpecialTaskProjection findByTaskId(@Param("taskId") Integer taskId);

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO agrosys_task.SPECIAL_TASK (task_id, worker_id, payment_amount)
            VALUES (:taskId, :workerId, :paymentAmount)
            """, nativeQuery = true)
    Integer insertSpecialTask(
            @Param("taskId") Integer taskId,
            @Param("workerId") Integer workerId,
            @Param("paymentAmount") java.math.BigDecimal paymentAmount);

    @Modifying
    @Transactional
    @Query(value = """
            UPDATE agrosys_task.SPECIAL_TASK
            SET worker_id = :workerId,
                payment_amount = :paymentAmount
            WHERE task_id = :taskId
            """, nativeQuery = true)
    Integer updateSpecialTask(
            @Param("taskId") Integer taskId,
            @Param("workerId") Integer workerId,
            @Param("paymentAmount") java.math.BigDecimal paymentAmount);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM agrosys_task.SPECIAL_TASK WHERE task_id = :taskId", nativeQuery = true)
    void deleteSpecialTaskById(@Param("taskId") Integer taskId);

    @Query(value = "SELECT COUNT(*) FROM agrosys_task.SPECIAL_TASK WHERE task_id = :taskId", nativeQuery = true)
    int countByTaskId(@Param("taskId") Integer taskId);
}