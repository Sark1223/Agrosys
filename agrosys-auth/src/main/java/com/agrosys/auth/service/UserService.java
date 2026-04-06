package com.agrosys.auth.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.agrosys.auth.dto.Response;
import com.agrosys.auth.dto.User.RegisterRequest;
import com.agrosys.auth.dto.User.RegisterResponse;
import com.agrosys.auth.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public List<UserRepository.getUser> getAllUsers() {
        return userRepository.findAllUsers();
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        log.info("[REQUEST] - request: {}", request);

        if (userRepository.existsByUserName(request.getUserName()) != null) {
            log.warn("[FIELD VIOLATION] - El nombre de usuario ya está en uso: {}", request.getUserName());
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        Integer user = userRepository.insertUser(request.getFirstName(), request.getLastName(), request.getUserName(),
                encodedPassword, request.getRolId());

        if (user == null || user == 0) {
            log.error("[FAILED] - No se pudo registrar el usuario");
            throw new RuntimeException("No se pudo registrar el usuario");
        }

        UserRepository.UserProjection savedUser = userRepository.findByUserName(request.getUserName());
        if (savedUser == null) {
            log.error("[FAILED] - Error al recuperar el usuario registrado: {}", request.getUserName());
            throw new RuntimeException("No se pudo recuperar el usuario registrado");
        }
        log.info("[SUCCESS] - Usuario registrado exitosamente: {} con ID: {}",
                savedUser.getNamePorfile(), savedUser.getUserId());

        // 7. Construir respuesta
        return RegisterResponse.builder()
                .userId(savedUser.getUserId())
                .userName(savedUser.getNamePorfile())
                .message("Usuario registrado exitosamente")
                .success(true)
                .build();
    }

    @Transactional
    public RegisterResponse updateUser(RegisterRequest request) {
        log.info("[REQUEST] - request: {}", request);

        if (userRepository.existsByUserName(request.getUserName(), request.getUserId()) != null) {
            log.warn("[FIELD VIOLATION] - El nombre de usuario ya está en uso: {}", request.getUserName());
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }

        Integer update = userRepository.updateUser(request.getFirstName(), request.getLastName(), request.getUserName(),
                request.getRolId(), request.getUserId());

        if (update == null || update == 0) {
            log.error("[FAILED] - Error al actualizar");
            throw new RuntimeException("No se pudo registrar el usuario");
        }
        log.info("[SUCCESS] - Usuario actualizado exitosamente: {} con ID: {}",
                request.getFirstName() + " " + request.getLastName(), request.getUserId());

        // 7. Construir respuesta
        return RegisterResponse.builder()
                .userId(request.getUserId())
                .userName(request.getFirstName() + " " + request.getLastName())
                .message("Usuario actualizado exitosamente")
                .success(true)
                .build();
    }

    @Transactional
    public Response updatePasswordUser(Integer userId, String newPassword) {
        log.info("[REQUEST] - userId: {}, newPassword: {}", userId, newPassword);

        if(userId.equals(1))
            return Response.builder()
                    .success(false)
                    .message("No se puede actualizar la contraseña del usuario administrador")
                    .data(null)
                    .build();

        String encodedPassword = passwordEncoder.encode(newPassword);

        Integer update = userRepository.updatePasswordUser(userId, encodedPassword);

        if (update == null || update == 0) {
            log.error("[FAILED] - Error al actualizar la contraseña");
            throw new RuntimeException("No se pudo actualizar la contraseña");
        }
        // 7. Construir respuesta
        return Response.builder()
                .success(true)
                .message("Contraseña de usuario actualizada exitosamente")
                .data(update)
                .build();
    }

    @Transactional
    public Response deleteUser(Integer userId) {
        log.info("[REQUEST] - request: {}", userId); 
        
        if(userId.equals(1))
            return Response.builder()
                    .success(false)
                    .message("No se puede eliminar el usuario administrador")
                    .data(null)
                    .build();

        Integer delete = userRepository.deleteUser(userId);
        if (delete == null || delete == 0) {
            log.error("[FAILED] - Error al eliminar el usuario");
            throw new RuntimeException("No se pudo eliminar el usuario");
        }

        log.info("[SUCCESS] - Usuario eliminado exitosamente"); 
        
        // 7. Construir respuesta
        return Response.builder()
                .success(true)
                .message("Usuario eliminado exitosamente")
                .data(delete)
                .build();
    }
}
