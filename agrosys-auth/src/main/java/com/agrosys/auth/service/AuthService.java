package com.agrosys.auth.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.agrosys.auth.dto.Auth.LoginRequest;
import com.agrosys.auth.dto.Auth.LoginResponse;
import com.agrosys.auth.repository.UserRepository;
import com.agrosys.auth.security.JwtService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
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

}