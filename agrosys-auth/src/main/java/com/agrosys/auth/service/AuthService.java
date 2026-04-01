package com.agrosys.auth.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.agrosys.auth.dto.LoginRequest;
import com.agrosys.auth.dto.LoginResponse;
import com.agrosys.auth.dto.RegisterRequest;
import com.agrosys.auth.dto.RegisterResponse;
import com.agrosys.auth.repository.UserRepository;
import com.agrosys.auth.security.JwtService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService tokenProvider;
    private final AuthenticationManager authenticationManager;

    public LoginResponse login(LoginRequest request) {
        log.info("[REQUEST] - request: {}", request);

        // 1. Autenticar con Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUserName(),
                        request.getPassword()));

        UserRepository.UserProjection user = userRepository.findByUserName(request.getUserName());

        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<String> modules = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(auth -> auth.startsWith("MODULE_"))
                .collect(Collectors.toList());

        String token = tokenProvider.generateToken(request.getUserName(), user.getRolName(), modules);

        Integer updated = userRepository.updateTokenByUserName(token, request.getUserName());
        if (updated == null || updated == 0) {
            log.warn("No se pudo actualizar el token para el usuario: {}", request.getUserName());
        } else {
            log.debug("Token actualizado en base de datos para usuario: {}", request.getUserName());
        }

        log.info("[REQUEST] - [SUCCESS][AUTENTICACION]");

        return LoginResponse.builder()
                .token(token)
                .userName(user.getNamePorfile())
                .userId(user.getUserId())
                .rol(user.getRolName() != null ? user.getRolName() : "USER")
                .modules(modules)
                .expiresIn(tokenProvider.getExpirationDateFromToken(token))
                .build();
    }

    /**
     * Valida un token JWT
     */
    public boolean validateToken(String token) {
        boolean isValid = tokenProvider.validateToken(token);
        if (isValid) {
            String username = tokenProvider.getUsernameFromToken(token);
            log.debug("[SUCCESS] - Token válido para usuario: {}", username);
        } else {
            log.debug("[FAILED] - Token inválido");
        }
        return isValid;
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        log.info("[REQUEST] - request: {}", request);

        if (userRepository.existsUserById(request.getUserId()) != null) {
            log.warn("[FIELD VIOLATION] - El ID de usuario ya está en uso: {}", request.getUserId());
            throw new RuntimeException("El ID de usuario ya está en uso");
        }

        if (userRepository.existsByUserName(request.getUserName()) != null) {
            log.warn("[FIELD VIOLATION] - El nombre de usuario ya está en uso: {}", request.getUserName());
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        Integer user = userRepository.insertUser(request.getUserId(), request.getUserName(), encodedPassword,
                request.getRolId());

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
}