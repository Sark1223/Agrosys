package com.agrosys.plot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.agrosys.plot.entity.AgrosysPlantatio;

import jakarta.transaction.Transactional;

@Repository
public interface PlantatioRespository extends JpaRepository<AgrosysPlantatio, Integer> {

        interface PlantatioProjection {
                Integer getPlantatioId();

                String getName();

                String getStartAt();

                String getEndAt();

                String getNotas();

                Integer getPlotId();
        }

        @Query(value = """
                        SELECT
                                plantatioId,
                                name,
                                start_at,
                                end_at,
                                notas,
                                plotId
                        FROM agrosys_db.plantatio
                        WHERE plotId = :plotId
                        ORDER BY plantatioId DESC
                        """, nativeQuery = true)
        List<PlantatioProjection> findByPlotId(Integer plotId);

        @Query(value = """
                        SELECT plantatioId
                        FROM agrosys_db.plantatio
                        WHERE name = :name
                        """, nativeQuery = true)
        Integer existsByName(String name);

        @Query(value = """
                        SELECT start_at FROM agrosys_db.plantatio WHERE plantatioId = :plantatioId
                        """, nativeQuery = true)
        String getStartAtPlantatio(Integer plantatioId);

        @Query(value = """
                        SELECT end_at FROM agrosys_db.plantatio WHERE plantatioId = :plantatioId
                        """, nativeQuery = true)
        String getEndAtPlantatio(Integer plantatioId);

        @Transactional
        @Modifying
        @Query(value = """
                        INSERT INTO agrosys_db.plantatio (name, start_at, notas, plotId)
                        VALUES (:name, :start_at, :notas, :plotId)
                        """, nativeQuery = true)
        Integer insertPlantatio(Integer plotId, String name, String start_at, String notas);

        @Query(value = """
                        SELECT plantatioId
                        FROM agrosys_db.plantatio
                        WHERE name = :name AND plantatioId != :plantatioId
                        """, nativeQuery = true)
        Integer existsByName(String name, Integer plantatioId);

        @Transactional
        @Modifying
        @Query(value = """
                        UPDATE agrosys_db.plantatio
                        SET name = :name, start_at = :start_at, notas = :notas, plotId = :plotId
                        WHERE plantatioId = :plantatioId
                        """, nativeQuery = true)
        Integer updatePlantatio(Integer plantatioId, Integer plotId, String name, String start_at, String notas);

        @Transactional
        @Modifying
        @Query(value = """
                        UPDATE agrosys_db.plantatio
                        SET name = :name, start_at = :start_at, end_at = :end_at, notas = :notas, plotId = :plotId
                        WHERE plantatioId = :plantatioId
                        """, nativeQuery = true)
        Integer updatePlantatio(Integer plantatioId, Integer plotId, String name, String start_at, String end_at,
                        String notas);

        @Transactional
        @Modifying
        @Query(value = """
                        UPDATE agrosys_db.plantatio
                        SET end_at = :end_at
                        WHERE plantatioId = :plantatioId
                        """, nativeQuery = true)
        Integer endPlantatio(Integer plantatioId, String end_at);

        @Transactional
        @Modifying
        @Query(value = """
                        DELETE FROM agrosys_db.plantatio
                        WHERE plantatioId = :plantatioId
                        """, nativeQuery = true)
        Integer deletePlantatio(Integer plantatioId);

        // =========================== METODOS PARA ESTADOS ===========================
        @Query(value = """
                        SELECT plotId, name FROM agrosys_db.plot
                        WHERE plotId = :plotId
                        """, nativeQuery = true)
        List<PlotRepository.PlotProjection> getAllPlotsSelected(Integer plotId);

        

        interface DatesPlantationProjection {
                String getStart_at();

                String getEnd_at();

                String getStages();
        }
        @Query(value = """
                        SELECT
                                p.start_at,
                                p.end_at,
                                json_arrayagg(
                                        JSON_OBJECT(
                                                'stageId', r.stageId,
                                                'startAtStage', r.start_at,
                                                'endAtStage', r.end_at
                                        )
                                ) as 'stages'
                        FROM agrosys_db.plantatio p
                        LEFT JOIN agrosys_db.plantatio_stage_relation r ON r.plantatioId = p.plantatioId
                        WHERE p.plantatioId = :plantatioId
                        GROUP BY p.plantatioId
                                                """, nativeQuery = true)
        DatesPlantationProjection getDatesPlantatioById(Integer plantatioId);

        @Query(value = """
                        SELECT start_at FROM agrosys_db.plantatio_stage_relation WHERE plantatioId = :plantatioId AND stageId = :stageId
                        """, nativeQuery = true)
        String getStartAtPlantatioStage(Integer plantatioId, Integer stageId);

        @Query(value = """
                        SELECT end_at FROM agrosys_db.plantatio_stage_relation WHERE plantatioId = :plantatioId AND stageId = :stageId
                        """, nativeQuery = true)
        String getEndAtPlantatioStage(Integer plantatioId, Integer stageId);

        interface DatesProjection {
                String getStart_at();

                String getEnd_at();
        }
        @Query(value = """
                        SELECT
                                end_at,
                                start_at
                        FROM agrosys_db.plantatio_stage_relation
                        WHERE plantatioId = :plantatioId AND stageId = :stageId
                        """, nativeQuery = true)
        DatesProjection getDatesPlantatioStage(Integer plantatioId, Integer stageId);

        @Transactional
        @Modifying
        @Query(value = """
                        INSERT INTO agrosys_db.plantatio_stage_relation (plantatioId, stageId, start_at, notes)
                        VALUES (:plantatioId, :stageId, :start_at, :notas)
                        """, nativeQuery = true)
        Integer insertPlantatioStage(Integer plantatioId, Integer stageId, String start_at, String notas);

        @Transactional
        @Modifying
        @Query(value = """
                        INSERT INTO agrosys_db.plantatio_stage_relation (plantatioId, stageId, start_at, end_at, notes)
                        VALUES (:plantatioId, :stageId, :start_at, :end_at, :notas)
                        """, nativeQuery = true)
        Integer insertPlantatioStage(Integer plantatioId, Integer stageId, String start_at, String end_at,
                        String notas);

        @Transactional
        @Modifying
        @Query(value = """
                        UPDATE agrosys_db.plantatio_stage_relation
                        SET end_at = :end_at
                        WHERE plantatioId = :plantatioId AND stageId = :stageId
                        """, nativeQuery = true)
        Integer endPlantatioStage(Integer plantatioId, Integer stageId, String end_at);

        @Transactional
        @Modifying
        @Query(value = """
                        UPDATE agrosys_db.plantatio_stage_relation
                        SET start_at = :start_at, end_at = :end_at, notes = :notas
                        WHERE plantatioId = :plantatioId AND stageId = :stageId
                        """, nativeQuery = true)
        Integer updatePlantatioStageNotas(Integer plantatioId, Integer stageId, String start_at, String end_at,
                        String notas);

        @Transactional
        @Modifying
        @Query(value = """
                        UPDATE agrosys_db.plantatio_stage_relation
                        SET end_at = :end_at
                        WHERE plantatioId = :plantatioId AND stageId = :stageId
                        """, nativeQuery = true)
        Integer updatePlantatioEndDate(Integer plantatioId, Integer stageId, String end_at);

        @Transactional
        @Modifying
        @Query(value = """
                        UPDATE agrosys_db.plantatio
                        SET start_at = :start_at
                        WHERE plantatioId = :plantatioId
                        """, nativeQuery = true)
        Integer updatePlantatioStartDate(Integer plantatioId, String start_at);

        @Transactional
        @Modifying
        @Query(value = """
                        DELETE FROM agrosys_db.plantatio_stage_relation
                        WHERE plantatioId = :plantatioId AND stageId = :stageId
                        """, nativeQuery = true)
        Integer deletePlantatioStage(Integer plantatioId, Integer stageId);

        interface PlantatioStageProjection {
                Integer getId();

                String getName();

                String getStart_at();

                String getEnd_at();

                String getNotes();
        }

        @Query(value = """
                        SELECT
                                r.stageId as id,
                                s.name,
                                r.start_at,
                                coalesce(r.end_at, "Sin fecha") as end_at,
                                r.notes
                        FROM agrosys_db.plantatio_stage_relation r
                        LEFT JOIN agrosys_db.plantatio_stage s ON s.stageId = r.stageId
                        WHERE plantatioId = :plantatioId
                        """, nativeQuery = true)
        List<PlantatioStageProjection> getStagesByPlantatio(Integer plantatioId);

}
