package com.agrosys.auth.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agrosys.auth.dto.RegisterRequest;
import com.agrosys.auth.dto.RegisterResponse;
import com.agrosys.auth.dto.Response;
import com.agrosys.auth.repository.UserRepository;
import com.agrosys.auth.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    
    @PostMapping("/register")
    @PreAuthorize("hasAuthority('MODULE_USUARIOS')")
    public ResponseEntity<Object> register(@Valid @RequestBody RegisterRequest request) {
        log.info("[REQUEST] - request: {}", request);
        RegisterResponse response = userService.register(request);
        Response successResponse = Response.builder()
                .success(true)
                .message("Usuario registrado exitosamente")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(successResponse);
    }

    @GetMapping("/get-all")
    @PreAuthorize("hasAuthority('MODULE_USUARIOS')")
    public ResponseEntity<Object> getAllUsers() {
        List<UserRepository.getUser> users = userService.getAllUsers();
        Response successResponse = Response.builder()
                .success(true)
                .message("Usuarios obtenidos exitosamente")
                .data(users)
                .build();
        return ResponseEntity.ok(successResponse);
    }

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('MODULE_USUARIOS')")
    public ResponseEntity<Object> updateUser(@Valid @RequestBody RegisterRequest request) {
        log.info("[REQUEST] - request: {}", request);
        RegisterResponse response = userService.updateUser(request);
        Response successResponse = Response.builder()
                .success(true)
                .message("Usuario actualizado exitosamente")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(successResponse);
    }
}