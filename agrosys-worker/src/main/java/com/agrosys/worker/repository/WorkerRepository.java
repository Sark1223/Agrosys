package com.agrosys.worker.repository;

import java.math.BigDecimal;
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

        interface WeeklySalaryProjection {
                Integer getWorkerId();
                String getName();
                BigDecimal getHourlyPay();
                LocalDate getDate(); 
                Integer getHoursWorked();
                BigDecimal getExtras(); 
        }

        interface WorkerProjection {

        Integer getWorkerId();

        String getName();

        String getNotas();

        java.math.BigDecimal getSalary();

        String getPhoto();

        String getPhotoPublicId();

        BigDecimal getHourlyPay();
    }

    @Query(value = """
            SELECT
                w.workerId,
                w.name,
                w.notas,
                w.salary,
                w.photo,
                w.photo_public_id,
                w.hourly_pay
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
                w.photo_public_id,
                w.hourly_pay
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
            INSERT INTO agrosys_worker.WORKER (name, notas, salary, photo, photo_public_id, hourly_pay )
            VALUES (:name, :notas, :salary, :photo, :photoPublicId, :hourlyPay)
            """, nativeQuery = true)
    Integer insertWorker(String name, String notas, java.math.BigDecimal salary, String photo, String photoPublicId, BigDecimal hourlyPay);

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
                w.photo_public_id,
                w.hourly_pay
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
                        SET name = :name, notas = :notas, salary = :salary, photo = :photo, photo_public_id = :photoPublicId, hourly_pay = :hourlyPay
                        WHERE workerId = :id
                        """, nativeQuery = true)
    Integer updateWorker(@Param("id") Integer id, @Param("name") String name,
            @Param("notas") String notas, @Param("salary") java.math.BigDecimal salary,
            @Param("photo") String photo, @Param("photoPublicId") String photoPublicId, @Param("hourlyPay") BigDecimal hourlyPay);

    
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

    
    interface AttendanceHistoryProjection {

        Integer getAttendanceId();

        Integer getWorkerId();

        String getWorkerName();

        LocalDate getDate();

        Boolean getAttended();

        Integer getHoursWorked();
    }

    // La consulta con filtros, paginación y el JOIN para traer el nombre
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

        // =========================================================================
        // ================= VALIDAR QUE CHECO ASISTENCIA ==================
        // =========================================================================
        @Query(value = """
                        SELECT COUNT(*) > 0 AS asistencia
                        FROM agrosys_worker.ATTENDANCE
                        WHERE DATE(`date`) = CURDATE()
                                """, nativeQuery = true)
        int asistencia();

        // ================= REPORTE SEMANAL DE SALARIOS ===================
        @Query(value = """
                        SELECT
                            w.workerId,
                            w.name,
                            w.hourly_pay AS hourlyPay,
                            a.date,
                            a.hoursWorked,
                            COALESCE(SUM(st.payment_amount), 0) AS extras /*si la suma es null (porque no tiene tareas), pon 0 en su lugar*/
                        FROM agrosys_worker.WORKER w
                        LEFT JOIN agrosys_worker.ATTENDANCE a
                            ON w.workerId = a.workerId
                            AND a.date BETWEEN :weekStart AND :weekEnd
                        LEFT JOIN agrosys_task.WORKER_TASK wt
                            ON w.workerId = wt.worker_id
                        LEFT JOIN agrosys_task.SPECIAL_TASK st
                            ON wt.special_task_id = st.special_task_id
                            AND DATE(st.create_at) BETWEEN :weekStart AND :weekEnd /*extrae solo la fecha del campo datetime*/
                        GROUP BY w.workerId, w.name, w.hourly_pay, a.date, a.hoursWorked
                        ORDER BY w.name, a.date
                        """, nativeQuery = true)
        List<WeeklySalaryProjection> getWeeklySalaryReport(
                        @Param("weekStart") LocalDate weekStart,
                        @Param("weekEnd") LocalDate weekEnd);
}
