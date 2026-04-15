package com.agrosys.auth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.agrosys.auth.entity.AgrosysRol;

import jakarta.transaction.Transactional;

@Repository
public interface RolRepository extends JpaRepository<AgrosysRol, Integer> {

        interface RolProjection {
                Integer getRolId();

                String getName();

                String getDescription();

                String getModulos();
        }

        @Query(value = """
                        SELECT
                                r.rolId,
                                r.name ,
                                r.description,
                                json_arrayagg(rm.moduleId) as modulos
                        FROM agrosys_auth.ROL r
                        LEFT JOIN agrosys_auth.ROL_MODULE rm ON r.rolId = rm.rolId
                        GROUP BY r.rolId, r.name, r.description;
                                                        """, nativeQuery = true)
        List<RolProjection> findAllRoles();

        @Query(value = """
                                SELECT
                                        r.rolId,
                                        r.name ,
                                        r.description,
                                        json_arrayagg(rm.moduleId) as modulos
                                FROM agrosys_auth.ROL r
                                LEFT JOIN agrosys_auth.ROL_MODULE rm ON r.rolId = rm.rolId
                        WHERE r.rolId = :rolId;
                                                                                """, nativeQuery = true)
        RolProjection loadRolById(Integer rolId);

        @Modifying
        @Query(value = """
                        INSERT INTO agrosys_auth.ROL (name, description)
                        VALUES (:name, :description)
                        """, nativeQuery = true)
        Integer insertRol(String name, String description);

        @Query(value = """
                        SELECT LAST_INSERT_ID();
                                        """, nativeQuery = true)
        Integer getLastInsert();

        @Transactional
        @Modifying
        @Query(value = """
                        INSERT INTO agrosys_auth.ROL_MODULE (moduleId, rolId)
                        SELECT m.moduleId, :rolId
                        FROM agrosys_auth.MODULE m
                        WHERE m.moduleId IN (:moduleIds)
                        """, nativeQuery = true)
        Integer insertRolModules(Integer rolId, List<Integer> moduleIds);

        @Query(value = """
                        SELECT rolId
                        FROM agrosys_auth.ROL
                        WHERE name = :name
                        """, nativeQuery = true)
        Integer existsByName(String name);

        @Query(value = """
                        SELECT rolId
                        FROM agrosys_auth.ROL
                        WHERE name = :name AND rolId != :excludeRolId
                        """, nativeQuery = true)
        Integer existsByName(String name, Integer excludeRolId);

        @Transactional
        @Modifying
        @Query(value = """
                        UPDATE agrosys_auth.ROL
                        SET
                                name = :name,
                                description = :description
                        WHERE rolId = :rolId
                        """, nativeQuery = true)
        Integer updateRol(Integer rolId, String name, String description);

        @Query(value="""
                        SELECT moduleId
                        FROM agrosys_auth.ROL_MODULE
                        WHERE rolId = :rolId
                        """, nativeQuery = true)
        List<Integer> getModulesByRolId(Integer rolId);

        @Transactional
        @Modifying
        @Query(value = """
                        DELETE FROM agrosys_auth.ROL_MODULE
                        WHERE rolId = :rolId
                        """, nativeQuery = true)
        Integer deleteRolModules(Integer rolId);

        @Transactional
        @Modifying
        @Query(value = """
                        DELETE FROM agrosys_auth.ROL_MODULE
                        WHERE rolId = :rolId AND moduleId IN (:moduleIds)
                        """, nativeQuery = true)
        Integer deleteRolModules(Integer rolId, List<Integer> moduleIds);

        @Query(value = """
                        SELECT COUNT(*)
                        FROM agrosys_auth.USER
                        WHERE rolId = :rolId
                        """, nativeQuery = true)
        Integer countUsersByRolId(Integer rolId);

        @Query(value = """
                        SELECT rolId FROM agrosys_auth.USER
                        WHERE rolId = :rolId
                        LIMIT 1
                        """, nativeQuery = true)
        Integer existsUserWithRol(Integer rolId);

        @Transactional
        @Modifying
        @Query(value = """
                        DELETE FROM agrosys_auth.ROL
                        WHERE rolId = :rolId AND rolId NOT IN (SELECT DISTINCT rolId FROM agrosys_auth.USER)
                                                """, nativeQuery = true)
        Integer deleteRol(Integer rolId);

}