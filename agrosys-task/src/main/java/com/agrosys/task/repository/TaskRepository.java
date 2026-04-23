package com.agrosys.task.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.agrosys.task.entity.AgrosysTask;

import jakarta.transaction.Transactional;

@Repository
public interface TaskRepository extends JpaRepository<AgrosysTask, Integer> {

    interface TaskProjection {

        Integer getTaskId();

        String getName();

        String getDescription();

        LocalDate getCreateAt();

        LocalDate getEndAt();

        Integer getTaskStageId();

        Integer getPlantationId();
    }

    // Buscar tarea por ID 
    @Query(value = """
            SELECT
                t.task_id,
                t.name,
                t.description,
                t.create_at,
                t.end_at,
                t.task_stage_id,
                t.plantation_id
            FROM agrosys_task.TASK t
            WHERE t.task_id = :id
            """, nativeQuery = true)
    TaskProjection findTaskById(@Param("id") Integer id);

    // Obtener todas las tareas
    @Query(value = """
            SELECT
                t.task_id,
                t.name,
                t.description,
                t.create_at,
                t.end_at,
                t.task_stage_id,
                t.plantation_id
            FROM agrosys_task.TASK t
            ORDER BY t.create_at DESC
            """, nativeQuery = true)
    List<TaskProjection> findAllTasks();

    // Verificar si existe un nombre de tarea (opcional)
    @Query(value = """
            SELECT task_id
            FROM agrosys_task.TASK
            WHERE name = :name
            LIMIT 1
            """, nativeQuery = true)
    Integer existsByName(@Param("name") String name);

    // Insertar nueva tarea
    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO agrosys_task.TASK (name, description, create_at, end_at, task_stage_id, plantation_id)
            VALUES (:name, :description, :createAt, :endAt, :taskStageId, :plantationId)
            """, nativeQuery = true)
    Integer insertTask(
            @Param("name") String name,
            @Param("description") String description,
            @Param("createAt") LocalDate createAt,
            @Param("endAt") LocalDate endAt,
            @Param("taskStageId") Integer taskStageId,
            @Param("plantationId") Integer plantationId
    );

    // Eliminar tarea por ID (nativo)
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM agrosys_task.TASK WHERE task_id = :id", nativeQuery = true)
    void deleteTaskById(@Param("id") Integer id);

    // Contar por ID 
    @Query(value = "SELECT COUNT(*) FROM agrosys_task.TASK WHERE task_id = :id", nativeQuery = true)
    int countById(@Param("id") Integer id);

    @Query(value = "SELECT LAST_INSERT_ID()", nativeQuery = true)
    Integer getLastInsertId();

    @Modifying
    @Transactional
    @Query(value = """
        UPDATE agrosys_task.TASK
        SET name = :name,
            description = :description,
            create_at = :createAt,
            end_at = :endAt,
            task_stage_id = :taskStageId,
            plantation_id = :plantationId
        WHERE task_id = :id
        """, nativeQuery = true)
    int updateTask(
            @Param("id") Integer id,
            @Param("name") String name,
            @Param("description") String description,
            @Param("createAt") LocalDate createAt,
            @Param("endAt") LocalDate endAt,
            @Param("taskStageId") Integer taskStageId,
            @Param("plantationId") Integer plantationId
    );
}
