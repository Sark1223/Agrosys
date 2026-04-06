package com.agrosys.auth.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agrosys.auth.dto.Auth.LoginRequest;
import com.agrosys.auth.dto.Auth.LoginResponse;
import com.agrosys.auth.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("[REQUEST] - request: {}", request);
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(
            @RequestHeader("Authorization") String authorizationHeader) {

        Map<String, Object> response = new HashMap<>();

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            boolean isValid = authService.validateToken(token);

            response.put("valid", isValid);
            if (isValid) {
                response.put("message", "Token válido");
            } else {
                response.put("message", "Token inválido");
            }
        } else {
            response.put("valid", false);
            response.put("message", "Token no proporcionado o formato incorrecto");
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {

        // En un sistema real, podrías invalidar el token en la BD
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logout exitoso");

        log.info("[REQUEST] - Logout request received");
        return ResponseEntity.ok(response);
    }
}
