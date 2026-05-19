package com.agrosys.plot.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.agrosys.plot.entity.AgrosysLot;

import jakarta.transaction.Transactional;

@Repository
public interface LotRepository extends JpaRepository<AgrosysLot, Integer> {

        interface ConfigsProjection {
                Integer getId();

                String getName();

                byte getActive();

                String getOrigin();

                Integer getQty();
        }

        @Query(value = """
                        SELECT
                                id,
                                name,
                                active,
                                origin,
                                qyt
                        FROM (
                                SELECT tm.tradeId AS id, tm.nombre AS name, tm.active, 'tm' AS origin, count(lt.tradeMode) AS qyt
                                FROM agrosys_db.TRADE_MODE AS tm
                                LEFT JOIN agrosys_db.LOT_TRADE lt ON lt.tradeMode = tm.tradeId
                                GROUP BY tm.tradeId

                                UNION ALL

                                SELECT pu.unitId AS id, pu.nombre AS name, pu.active, 'pu' AS origin, count(lt.unitType) AS qyt
                                FROM agrosys_db.PRODUCT_UNIT AS pu
                                LEFT JOIN agrosys_db.LOT_TRADE lt ON lt.unitType =  pu.unitId
                                GROUP BY pu.unitId

                                UNION ALL

                                SELECT pt.typeId AS id, pt.nombre AS name, pt.active, 'pt' AS origin, count(lt.productType) AS qyt
                                FROM agrosys_db.PRODUCT_TYPE AS pt
                                LEFT JOIN agrosys_db.LOT_TRADE lt ON lt.productType =  pt.typeId
                                GROUP BY pt.typeId
                        ) AS configs;
                                """, nativeQuery = true)
        List<ConfigsProjection> findAllConfigs();

        // ======================== TRADE MODE ========================
        @Query(value = """
                        SELECT tradeId FROM agrosys_db.TRADE_MODE
                        WHERE nombre = :name
                        """, nativeQuery = true)
        Integer findTradeModeByName(String name);

        @Query(value = """
                        SELECT tradeId FROM agrosys_db.TRADE_MODE
                        WHERE nombre = :name AND tradeId != :id
                        """, nativeQuery = true)
        Integer findTradeModeByName(Integer id, String name);

        @Transactional
        @Modifying
        @Query(value = """
                        INSERT INTO agrosys_db.TRADE_MODE (nombre, active)
                        VALUES (:name, :active)
                        """, nativeQuery = true)
        Integer insertTradeMode(String name, Boolean active);

        @Transactional
        @Modifying
        @Query(value = """
                        UPDATE agrosys_db.TRADE_MODE
                        SET nombre = :name, active = :active
                        WHERE tradeId = :id
                        """, nativeQuery = true)
        Integer updateTradeMode(Integer id, String name, Boolean active);

        // ======================== PRODUCT UNIT ========================

        @Query(value = """
                        SELECT unitId FROM agrosys_db.PRODUCT_UNIT
                        WHERE nombre = :name
                        """, nativeQuery = true)
        Integer findProductUnitByName(String name);

        @Query(value = """
                        SELECT unitId FROM agrosys_db.PRODUCT_UNIT
                        WHERE nombre = :name AND unitId != :id
                        """, nativeQuery = true)
        Integer findProductUnitByName(Integer id, String name);

        @Transactional
        @Modifying
        @Query(value = """
                        INSERT INTO agrosys_db.PRODUCT_UNIT (nombre, active)
                        VALUES (:name, :active)
                        """, nativeQuery = true)
        Integer insertProductUnit(String name, Boolean active);

        @Transactional
        @Modifying
        @Query(value = """
                        UPDATE agrosys_db.PRODUCT_UNIT
                        SET nombre = :name, active = :active
                        WHERE unitId = :id
                        """, nativeQuery = true)
        Integer updateProductUnit(Integer id, String name, Boolean active);

        // ======================== PRODUCT TYPE ========================
        @Query(value = """
                        SELECT typeId FROM agrosys_db.PRODUCT_TYPE
                        WHERE nombre = :name
                        """, nativeQuery = true)
        Integer findProductTypeByName(String name);

        @Query(value = """
                        SELECT typeId FROM agrosys_db.PRODUCT_TYPE
                        WHERE nombre = :name AND typeId != :id
                        """, nativeQuery = true)
        Integer findProductTypeByName(Integer id, String name);

        @Transactional
        @Modifying
        @Query(value = """
                        INSERT INTO agrosys_db.PRODUCT_TYPE (nombre, active)
                        VALUES (:name, :active)
                        """, nativeQuery = true)
        Integer insertProductType(String name, Boolean active);

        @Query(value = """
                        SELECT COUNT(*) FROM agrosys_db.LOT_TRADE WHERE productType = :id;
                        """, nativeQuery = true)
        Integer findTradeWithProductType(Integer id);

        @Transactional
        @Modifying
        @Query(value = """
                        UPDATE agrosys_db.PRODUCT_TYPE
                        SET nombre = :name, active = :active
                        WHERE typeId = :id
                        """, nativeQuery = true)
        Integer updateProductType(Integer id, String name, Boolean active);

        // ======================== LOTS OF PLANTATION ========================

        interface SelectPlantationsProjection {
                Integer getPlantatioId();

                String getName();
        }

        @Query(value = """
                        SELECT DISTINCT
                            s.plantatioId,
                            p.name
                        FROM agrosys_db.plantatio_stage_relation s
                        LEFT JOIN agrosys_db.plantatio p ON p.plantatioId = s.plantatioId
                        WHERE s.stageId = 3 OR s.stageId = 4;
                        """, nativeQuery = true)
        List<SelectPlantationsProjection> findAllPlantations();

        @Query(value = """
                        SELECT lotId FROM agrosys_db.LOT WHERE name = :name AND plantationId = :plantationId;
                        """, nativeQuery = true)
        Integer findLotByName(String name, Integer plantationId);

        @Query(value = """
                        SELECT lotId FROM agrosys_db.LOT WHERE name = :name AND plantationId = :plantationId AND lotId != :lotId;
                        """, nativeQuery = true)
        Integer findLotByName(String name, Integer plantationId, Integer lotId);

        @Transactional
        @Modifying
        @Query(value = """
                        INSERT INTO agrosys_db.LOT (name, plantationId, description)
                        VALUES (:name, :plantationId, :description)
                        """, nativeQuery = true)
        Integer insertLot(String name, Integer plantationId, String description);

        @Transactional
        @Modifying
        @Query(value = """
                        UPDATE agrosys_db.LOT
                        SET name = :name, description = :description
                        WHERE lotId = :id
                        """, nativeQuery = true)
        Integer updateLot(Integer id, String name, String description);

        @Transactional
        @Modifying
        @Query(value = """
                        INSERT INTO agrosys_db.LOT_TRADE (loteId, productType, unitType, unitCost, tradeMode, freightCost)
                        VALUES (:lotId, :productType, :unitType, :unitCost, :tradeMode, :freightCost)
                        """, nativeQuery = true)
        Integer insertLotTrade(Integer lotId, Integer productType, Integer unitType, BigDecimal unitCost,
                        Integer tradeMode, BigDecimal freightCost);

        @Transactional
        @Modifying
        @Query(value = """
                        UPDATE agrosys_db.LOT_TRADE
                        SET productType = :productType, unitType = :unitType, unitCost = :unitCost, tradeMode = :tradeMode, freightCost = :freightCost
                        WHERE lotTradeId = :lotTradeId
                        """, nativeQuery = true)
        Integer updateLotTrade(Integer lotTradeId, Integer productType, Integer unitType, BigDecimal unitCost,
                        Integer tradeMode, BigDecimal freightCost);

        interface LotsByPlantationsProjection {
                Integer getLotId();

                String getName();

                String getFechaCreacion();

                String getDescription();

                Integer getProductType();

                String getNameTypeProduct();

                Integer getUnitType();

                String getNameUnitType();

                BigDecimal getUnitCost();

                Integer getTradeMode();

                String getNameTradeMode();

                BigDecimal getFreightCost();

                Integer getPlantationId();

                Integer getLotTradeId();
        }

        @Query(value = """
                        SELECT
                                l.lotId,
                                l.name,
                                l.fechaCreacion,
                                lt.productType,
                                pt.nombre AS nameTypeProduct,
                                lt.unitType,
                                ut.nombre AS nameUnitType,
                                lt.unitCost,
                                lt.tradeMode,
                                tm.nombre AS nameTradeMode,
                                lt.freightCost,
                                l.plantationId,
                                l.description,
                                lt.lotTradeId
                        FROM agrosys_db.LOT l
                        LEFT JOIN agrosys_db.LOT_TRADE lt ON lt.loteId = l.lotId
                        LEFT JOIN agrosys_db.PRODUCT_TYPE pt ON pt.typeId = lt.productType
                        LEFT JOIN agrosys_db.PRODUCT_UNIT ut ON ut.unitId = lt.unitType
                        LEFT JOIN agrosys_db.TRADE_MODE tm ON tm.tradeId = lt.tradeMode
                        WHERE plantationId = :id;
                        """, nativeQuery = true)
        List<LotsByPlantationsProjection> findAllLotsByPlantation(Integer id);

        @Transactional
        @Modifying
        @Query(value = """
                        DELETE FROM agrosys_db.LOT WHERE lotId = :id
                        """, nativeQuery = true)
        Integer deleteLot(Integer id);

        @Transactional
        @Modifying
        @Query(value = """
                        DELETE FROM agrosys_db.LOT_TRADE WHERE lotTradeId = :lotTradeId
                        """, nativeQuery = true)
        Integer deleteLotTrade(Integer lotTradeId);
}
