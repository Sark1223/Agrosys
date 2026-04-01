package com.agrosys.auth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.agrosys.auth.entity.AgrosysUser;

import jakarta.transaction.Transactional;

@Repository
public interface UserRepository extends JpaRepository<AgrosysUser, Integer> {

        interface UserProjection {
                Integer getUserId();

                String getRolName();

                String getNamePorfile();
        }

        @Query(value = """
                        SELECT
                                u.userId,
                                r.name as rolName,
                                CONCAT(u.firstName, ' ', u.lastName) as namePorfile
                        FROM agrosys_auth.USER u
                        LEFT JOIN agrosys_auth.ROL r ON r.rolId = u.rolId
                        WHERE u.userName = :user ;
                                                """, nativeQuery = true)
        UserProjection findByUserName(String user);

        interface UserCustom {
                String getUserName();

                String getPassword();

                Integer getRolId();

                String getRolName();

                String getModules();
        }

        @Query(value = """
                        SELECT
                                u.userName,
                                u.password,
                                u.rolId,
                                r.name as rolName,
                                JSON_ARRAYAGG(m.name) as modules
                        FROM agrosys_auth.USER u
                        LEFT JOIN agrosys_auth.ROL r ON r.rolId = u.rolId
                        LEFT JOIN agrosys_auth.ROL_MODULE rm ON r.rolId = rm.rolId
                        LEFT JOIN agrosys_auth.MODULE m ON rm.moduleId = m.moduleId
                        WHERE u.userName = :user;
                                        """, nativeQuery = true)
        UserCustom loadUserByUsername(String user);

        @Query(value = """
                        SELECT userId
                        FROM agrosys_auth.USER
                        WHERE userName = :id
                        """, nativeQuery = true)
        Integer existsUserById(Integer id);

        @Query(value = """
                        SELECT userId
                        FROM agrosys_auth.USER
                        WHERE userName = :user
                        """, nativeQuery = true)
        Integer existsByUserName(String user);

        @Modifying
        @Query(value = """
                        INSERT INTO agrosys_auth.USER (userId, userName, password, rolId)
                        VALUES (:userId, :userName, :password, :rolId)
                        """, nativeQuery = true)
        Integer insertUser(Integer userId, String userName, String password, Integer rolId);

        @Transactional
        @Modifying
        @Query(value = """
                        UPDATE agrosys_auth.`USER`
                        SET token = :token
                        WHERE userName = :userName
                                                """, nativeQuery = true)
        Integer updateTokenByUserName(String token, String userName);

        // Consulta para obtener los módulos a los que tiene acceso un rol específico

        @Query(value = """
                        SELECT m.name FROM agrosys_auth.ROL r
                        LEFT JOIN agrosys_auth.ROL_MODULE rm ON r.rolId = rm.rolId
                        LEFT JOIN agrosys_auth.MODULE m ON rm.moduleId = m.moduleId
                        WHERE r.rolId = :rolId;
                                        """, nativeQuery = true)
        List<String> findModulesByRollId(Integer rolId);

        @Query(value = """
                        SELECT r.name FROM agrosys_auth.ROL r
                        WHERE r.rolId = :rolId;
                                        """, nativeQuery = true)
        String getRolNameByRollId(Integer rolId);
}