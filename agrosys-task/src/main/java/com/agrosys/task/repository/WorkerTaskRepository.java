package com.agrosys.task.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.agrosys.task.entity.WorkerTask;

import jakarta.transaction.Transactional;

@Repository
public interface WorkerTaskRepository extends JpaRepository<WorkerTask, Integer> {

    @Query(value = "SELECT worker_id FROM agrosys_task.WORKER_TASK WHERE special_task_id = :specialTaskId", nativeQuery = true)
    List<Integer> findWorkerIdsBySpecialTaskId(@Param("specialTaskId") Integer specialTaskId);

    @Query(value = "SELECT special_task_id FROM agrosys_task.WORKER_TASK WHERE worker_id = :workerId", nativeQuery = true)
    List<Integer> findSpecialTaskIdsByWorkerId(@Param("workerId") Integer workerId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM agrosys_task.WORKER_TASK WHERE special_task_id = :specialTaskId", nativeQuery = true)
    void deleteBySpecialTaskId(@Param("specialTaskId") Integer specialTaskId);

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO agrosys_task.WORKER_TASK (worker_id, special_task_id)
            VALUES (:workerId, :specialTaskId)
            """, nativeQuery = true)
    void insertWorkerAssignment(
            @Param("workerId") Integer workerId,
            @Param("specialTaskId") Integer specialTaskId);

    @Query(value = """
            SELECT COUNT(*) > 0 FROM agrosys_task.WORKER_TASK
            WHERE worker_id = :workerId AND special_task_id = :specialTaskId
            """, nativeQuery = true)
    boolean existsByWorkerIdAndSpecialTaskId(
            @Param("workerId") Integer workerId,
            @Param("specialTaskId") Integer specialTaskId);
}
