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
        Integer insertWorker(String name, String notas, java.math.BigDecimal salary, String photo, String photoPublicId,
                        BigDecimal hourlyPay);

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
                        @Param("photo") String photo, @Param("photoPublicId") String photoPublicId,
                        @Param("hourlyPay") BigDecimal hourlyPay);

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
        // Método que nos faltaba para actualizar (Upsert)
        @Modifying
        @Transactional
        @Query(value = """
                        UPDATE agrosys_worker.ATTENDANCE
                        SET attended = :attended, hoursWorked = :hoursWorked
                        WHERE workerId = :workerId AND date = :date
                        """, nativeQuery = true)
        Integer updateAttendance(@Param("workerId") Integer workerId, @Param("date") LocalDate date,
                        @Param("attended") Boolean attended, @Param("hoursWorked") Integer hoursWorked);

        // Método para que el Frontend sepa si ya hay asistencia y cambie el botón a verde
        @Query(value = "SELECT COUNT(*) FROM agrosys_worker.ATTENDANCE WHERE date = :date", nativeQuery = true)
        int countAttendanceByDate(@Param("date") LocalDate date);             
}