package com.agrosys.worker.repository;

import java.time.LocalDate;
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

        // attention: para el módulo de asistencia, podríamos necesitar un método para
        // obtener solo los trabajadores activos, dependiendo de cómo definamos "activo"
        // en nuestro modelo de datos. Por ejemplo, si tuviéramos un campo "active" en
        // la tabla WORKER, podríamos agregar un método como este:
        @Query(value = "SELECT COUNT(*) FROM agrosys_worker.ATTENDANCE WHERE workerId = :workerId AND date = :date", nativeQuery = true)
        int existsAttendanceByWorkerAndDate(@Param("workerId") Integer workerId,
                        @Param("date") LocalDate date);

        @Modifying
        @Transactional
        @Query(value = """
                        INSERT INTO agrosys_worker.ATTENDANCE (workerId, date, attended, hoursWorked)
                        VALUES (:workerId, :date, :attended, :hoursWorked)
                        """, nativeQuery = true)
        Integer insertAttendance(@Param("workerId") Integer workerId, @Param("date") LocalDate date,
                        @Param("attended") Boolean attended, @Param("hoursWorked") Integer hoursWorked);

        // =========================================================================
        // ================= CONSULTAS DE HISTORIAL DE ASISTENCIA ==================
        // =========================================================================

        // 1. La proyección para mapear los resultados con el nombre del trabajador
        interface AttendanceHistoryProjection {
                Integer getAttendanceId();

                Integer getWorkerId();

                String getWorkerName();

                LocalDate getDate();

                Boolean getAttended();

                Integer getHoursWorked();
        }

        // 2. La consulta con filtros, paginación y el JOIN para traer el nombre
        @Query(value = """
                        SELECT
                            a.attendanceId,
                            a.workerId,
                            w.name as workerName,
                            a.date,
                            a.attended,
                            a.hoursWorked
                        FROM agrosys_worker.ATTENDANCE a
                        INNER JOIN agrosys_worker.WORKER w ON a.workerId = w.workerId
                        WHERE (:workerId IS NULL OR a.workerId = :workerId)
                        AND (:startDate IS NULL OR a.date >= :startDate)
                        AND (:endDate IS NULL OR a.date <= :endDate)
                        ORDER BY a.date DESC
                        """, nativeQuery = true)
        List<AttendanceHistoryProjection> getAttendanceHistory(
                        @Param("workerId") Integer workerId,
                        @Param("startDate") String startDate,
                        @Param("endDate") String endDate);
}