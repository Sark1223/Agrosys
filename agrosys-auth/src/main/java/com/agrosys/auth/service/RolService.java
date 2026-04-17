package com.agrosys.auth.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.agrosys.auth.dto.Response;
import com.agrosys.auth.dto.Rol.RolRegister;
import com.agrosys.auth.repository.RolRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RolService {

    private final RolRepository rolRepository;

    public List<RolRepository.RolProjection> getAllRoles() {
        return rolRepository.findAllRoles();
    }

    public RolRepository.RolProjection getRolById(Integer rolId) {
        return rolRepository.loadRolById(rolId);
    }

    @Transactional
    public Response createRol(RolRegister request) {

        Integer rol = rolRepository.existsByName(request.getName());
        if (rol != null) {
            throw new IllegalArgumentException("El nombre del rol ya existe");
        }

        Integer rolId = insertRol(request);

        if (rolId.equals(0)) {
            throw new RuntimeException("No se pudo registrar el rol");
        }

        if (request.getModulos() != null && !request.getModulos().isEmpty()) {
            // Insertar los módulos asociados al rol
            Integer relacionRolModule = rolRepository.insertRolModules(rolId, request.getModulos());
            if (relacionRolModule == null || relacionRolModule == 0) {
                throw new RuntimeException("No se pudieron asociar los módulos al rol");
            }
        }

        return Response.builder()
                .message("Rol registrado exitosamente")
                .success(true)
                .build();
    }

    public Response updateRol(Integer rolId, RolRegister request) {
        if (rolId == null || rolId == 0 || rolId == 1) {
            throw new IllegalArgumentException("El ID del rol es inválido");
        }

        Integer rol = rolRepository.existsByName(request.getName(), rolId);
        if (rol != null && !rol.equals(rolId)) {
            throw new IllegalArgumentException("El nombre del rol ya existe");
        }

        log.info("Actualizando rol: {}", request);

        Integer updated = rolRepository.updateRol(rolId, request.getName(), request.getDescription());

        log.info("updated rol: {}", updated);

        if (updated == null || updated == 0) {
            throw new RuntimeException("No se pudo actualizar el rol");
        }

        if (request.getModulos() == null || request.getModulos().isEmpty()) {
            // En caso de que no se envíen módulos, se eliminan los módulos asociados al rol
            log.info("ENTRE A ELIMINAR LOS MODULOS ASOCIADOS AL ROL");
            Integer deletedModules = rolRepository.deleteRolModules(rolId);
            log.info("SALI DE ELIMINAR LOS MODULOS ASOCIADOS AL ROL, deletedModules: {}", deletedModules);

            if (deletedModules == null) {
                throw new RuntimeException("No se pudo eliminar los módulos asociados al rol");
            }

        } else {
            log.info("ENTRE A ACTUALIZAR LOS MODULOS ASOCIADOS AL ROL");
            // Obtener los módulos actualmente asociados al rol
            List<Integer> currentModules = rolRepository.getModulesByRolId(rolId);
            // List<Integer> newModules = request.getModulos();

            List<Integer> modulesToAdd = request.getModulos().stream()
                    .filter(moduleId -> !currentModules.contains(moduleId))
                    .toList();
            List<Integer> modulesToRemove = currentModules.stream()
                    .filter(moduleId -> !request.getModulos().contains(moduleId))
                    .toList();

            if (!modulesToRemove.isEmpty()) {
                Integer deletedModules = rolRepository.deleteRolModules(rolId, modulesToRemove);
                if (deletedModules == null) {
                    throw new RuntimeException("No se pudo eliminar los módulos asociados al rol");
                }

            }

            if (!modulesToAdd.isEmpty()) {
                Integer insertedModules = rolRepository.insertRolModules(rolId, modulesToAdd);
                if (insertedModules == null || insertedModules == 0) {
                    throw new RuntimeException("No se pudo asociar los módulos al rol");
                }
            }
        }

        return Response.builder()
                .message("Rol actualizado exitosamente")
                .success(true)
                .build();
    }

    public Response deleteRol(Integer rolId) {
        if (rolId == null || rolId == 0 || rolId == 1) {
            throw new IllegalArgumentException("No se puede eliminar el rol");
        }

        Integer userWithRol = rolRepository.existsUserWithRol(rolId);
        if (userWithRol != null) {
            throw new RuntimeException("No se puede eliminar el rol porque está en uso");
        }

        Integer deleted = rolRepository.deleteRol(rolId);
        if (deleted == null || deleted == 0) {
            throw new RuntimeException("No se pudo eliminar el rol");
        }
        return Response.builder()
                .message("Rol eliminado exitosamente")
                .success(true)
                .build();
    }

    @Transactional
    public Integer insertRol(RolRegister request){
        Integer rol = rolRepository.insertRol(request.getName(), request.getDescription());

        if(rol != null){
            return rolRepository.getLastInsert();
        }

        return 0;
    }
}
