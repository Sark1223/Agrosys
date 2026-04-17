package com.agrosys.plot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.agrosys.plot.entity.AgrosysPlot;

import jakarta.transaction.Transactional;

@Repository
public interface PlotRepository extends JpaRepository<AgrosysPlot, Integer> {

        interface PlotProjection {
                Integer getPlotId();

                String getName();

                String getDescription(); 

                String getPlantatios();
        }

        @Query(value = """
                WITH ranked AS (
                        SELECT
                                p.*,
                                ROW_NUMBER() OVER (
                                        PARTITION BY p.plotId
                                        ORDER BY p.plantatioId DESC
                                ) AS rn
                        FROM agrosys_db.plantatio p
                        )
                        SELECT
                                plt.plotId,
                                plt.name,
                                plt.description,
                                JSON_ARRAYAGG(
                                        CASE 
                                                WHEN r.plantatioId IS NOT NULL
                                                THEN JSON_OBJECT(
                                                        'plantatioId', r.plantatioId,
                                                        'name', r.name,
                                                        'start_at', r.start_at,
                                                        'end_at', COALESCE(r.end_at, 'N/A'),
                                                        'notas', r.notas
                                                )
                                                -- else '' / JSON_OBJECT()
                                        END
                        ) AS plantatios
                        FROM agrosys_db.plot plt
                        LEFT JOIN ranked r
                                ON r.plotId = plt.plotId AND r.rn <= 5
                        GROUP BY plt.plotId, plt.name, plt.description;
                                                                                """, nativeQuery = true)
        List<PlotProjection> findAllPlots();

        @Transactional
        @Modifying
        @Query(value = """
                        INSERT INTO agrosys_db.plot (name, description)
                        VALUES (:name, :description)
                        """, nativeQuery = true)
        Integer insertPlot(String name, String description);

        @Query(value = """
                        SELECT plotId
                        FROM agrosys_db.plot
                        WHERE name = :name
                        """, nativeQuery = true)
        Integer existsByName(String name);

        @Query(value = """
                        SELECT plotId
                        FROM agrosys_db.plot
                        WHERE name = :name AND plotId != :excludePlotId
                        """, nativeQuery = true)
        Integer existsByName(String name, Integer excludePlotId);

        @Transactional
        @Modifying
        @Query(value = """
                        UPDATE agrosys_db.plot
                        SET
                                name = :name,
                                description = :description
                        WHERE plotId = :plotId
                        """, nativeQuery = true)
        Integer updatePlot(Integer plotId, String name, String description);

        // @Transactional
        // @Modifying
        // @Query(value = """
        // DELETE FROM agrosys_auth.ROL
        // WHERE rolId = :rolId AND rolId NOT IN (SELECT DISTINCT rolId FROM
        // agrosys_auth.USER)
        // """, nativeQuery = true)
        // Integer deleteRol(Integer rolId);
}
