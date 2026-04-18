package com.agrosys.worker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.agrosys.worker.entity.AgrosysWorker;

import jakarta.transaction.Transactional;

@Repository
public interface WorkerRepository extends JpaRepository<AgrosysWorker, Integer> {

    interface WorkerProjection {

        Integer getWorkerId();

        String getName();

        String getNotas();
    }

    @Query(value = """
            SELECT
                w.workerId,
                w.name,
                w.notas
            FROM agrosys_worker.WORKER w
            WHERE w.name = :name
            """, nativeQuery = true)
    WorkerProjection findByName(String name);

    @Query(value = """
            SELECT
                w.workerId,
                w.name,
                w.notas
            FROM agrosys_worker.WORKER w
            """, nativeQuery = true)
    List<WorkerProjection> findAllWorkers();

    @Query(value = """
            SELECT workerId
            FROM agrosys_worker.WORKER
            WHERE name = :name
            """, nativeQuery = true)
    Integer existsByName(String name);

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO agrosys_worker.WORKER (name, notas)
            VALUES (:name, :notas)
            """, nativeQuery = true)
    Integer insertWorker(String name, String notas);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM agrosys_worker.WORKER WHERE workerId = :id", nativeQuery = true)
    void deleteWorkerById(@Param("id") Integer id);

    @Query(value = "SELECT COUNT(*) FROM agrosys_worker.WORKER WHERE workerId = :id", nativeQuery = true)
    int countById(@Param("id") Integer id);
}
