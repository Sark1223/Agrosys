package com.agrosys.task.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    interface SpecialTaskWithWorkersProjection {
        Integer getSpecialTaskId();
        String getName();
        BigDecimal getPaymentAmount();
        LocalDate getCreateAt();
        Integer getTaskStageId();
        LocalDate getEndAt();
        String getDescription();
        String getWorkerIds();
    }

    @Query(value = """
        SELECT st.special_task_id, st.name, st.payment_amount, st.create_at,
               st.task_stage_id, st.end_at, st.description,
               GROUP_CONCAT(wt.worker_id ORDER BY wt.worker_id SEPARATOR ',') AS worker_ids
        FROM agrosys_task.SPECIAL_TASK st
        LEFT JOIN agrosys_task.WORKER_TASK wt ON wt.special_task_id = st.special_task_id
        GROUP BY st.special_task_id
        ORDER BY st.create_at DESC
        """, nativeQuery = true)
    List<SpecialTaskWithWorkersProjection> findAllSpecialTasksWithWorkers();

    @Query(value = """
        SELECT st.special_task_id, st.name, st.payment_amount, st.create_at,
               st.task_stage_id, st.end_at, st.description,
               GROUP_CONCAT(wt.worker_id ORDER BY wt.worker_id SEPARATOR ',') AS worker_ids
        FROM agrosys_task.SPECIAL_TASK st
        INNER JOIN agrosys_task.WORKER_TASK wt ON wt.special_task_id = st.special_task_id
        WHERE wt.worker_id = :workerId
        GROUP BY st.special_task_id
        ORDER BY st.create_at DESC
        """, nativeQuery = true)
    List<SpecialTaskWithWorkersProjection> findSpecialTasksByWorkerId(@Param("workerId") Integer workerId);

    @Query(value = """
        SELECT st.special_task_id, st.name, st.payment_amount, st.create_at,
               st.task_stage_id, st.end_at, st.description,
               GROUP_CONCAT(wt.worker_id ORDER BY wt.worker_id SEPARATOR ',') AS worker_ids
        FROM agrosys_task.SPECIAL_TASK st
        LEFT JOIN agrosys_task.WORKER_TASK wt ON wt.special_task_id = st.special_task_id
        WHERE st.special_task_id = :specialTaskId
        GROUP BY st.special_task_id
        """, nativeQuery = true)
    SpecialTaskWithWorkersProjection findSpecialTaskWithWorkersById(@Param("specialTaskId") Integer specialTaskId);

    @Query(value = "SELECT COUNT(*) FROM agrosys_task.SPECIAL_TASK WHERE special_task_id = :specialTaskId", nativeQuery = true)
    int countBySpecialTaskId(@Param("specialTaskId") Integer specialTaskId);

    @Modifying
    @Transactional
    @Query(value = """
        UPDATE agrosys_task.SPECIAL_TASK
        SET name = :name,
            payment_amount = :paymentAmount,
            create_at = :createAt,
            task_stage_id = :taskStageId,
            end_at = :endAt,
            description = :description
        WHERE special_task_id = :specialTaskId
        """, nativeQuery = true)
    int updateSpecialTask(
            @Param("specialTaskId") Integer specialTaskId,
            @Param("name") String name,
            @Param("paymentAmount") BigDecimal paymentAmount,
            @Param("createAt") LocalDate createAt,
            @Param("taskStageId") Integer taskStageId,
            @Param("endAt") LocalDate endAt,
            @Param("description") String description);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM agrosys_task.SPECIAL_TASK WHERE special_task_id = :specialTaskId", nativeQuery = true)
    void deleteSpecialTaskById(@Param("specialTaskId") Integer specialTaskId);
}
