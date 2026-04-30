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

        java.math.BigDecimal getSalary();

        String getPhoto();

        String getPhotoPublicId();
    }

    @Query(value = """
            SELECT
                w.workerId,
                w.name,
                w.notas,
                w.salary,
                w.photo,
                w.photo_public_id
            FROM agrosys_worker.WORKER w
            WHERE w.name = :name
            """, nativeQuery = true)
    WorkerProjection findByName(String name);

    @Query(value = """
            SELECT
                w.workerId,
                w.name,
                w.notas,
                w.salary,
                w.photo,
                w.photo_public_id
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
            INSERT INTO agrosys_worker.WORKER (name, notas, salary, photo, photo_public_id)
            VALUES (:name, :notas, :salary, :photo, :photoPublicId)
            """, nativeQuery = true)
    Integer insertWorker(String name, String notas, java.math.BigDecimal salary, String photo, String photoPublicId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM agrosys_worker.WORKER WHERE workerId = :id", nativeQuery = true)
    void deleteWorkerById(@Param("id") Integer id);

    @Query(value = "SELECT COUNT(*) FROM agrosys_worker.WORKER WHERE workerId = :id", nativeQuery = true)
    int countById(@Param("id") Integer id);

    @Query(value = """
            SELECT
                w.workerId,
                w.name,
                w.notas,
                w.salary,
                w.photo,
                w.photo_public_id
            FROM agrosys_worker.WORKER w
            WHERE w.workerId = :id
            """, nativeQuery = true)
    WorkerProjection findByIdWorker(Integer id);

    @Query(value = "SELECT workerId FROM agrosys_worker.WORKER WHERE name = :name AND workerId != :excludeId", nativeQuery = true)
    Integer existsByNameExcludingId(@Param("name") String name, @Param("excludeId") Integer excludeId);

    @Modifying
    @Transactional
    @Query(value = """
            UPDATE agrosys_worker.WORKER 
            SET name = :name, notas = :notas, salary = :salary, photo = :photo, photo_public_id = :photoPublicId
            WHERE workerId = :id
            """, nativeQuery = true)
    Integer updateWorker(@Param("id") Integer id, @Param("name") String name, 
            @Param("notas") String notas, @Param("salary") java.math.BigDecimal salary, 
            @Param("photo") String photo, @Param("photoPublicId") String photoPublicId);
}