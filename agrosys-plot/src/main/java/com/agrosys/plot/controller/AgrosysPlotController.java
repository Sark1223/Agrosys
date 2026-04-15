package com.agrosys.plot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agrosys.plot.dto.Response;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/plot")
@RequiredArgsConstructor
@Slf4j
public class AgrosysPlotController {

    @GetMapping("/get-all")
    // @PreAuthorize("hasAuthority('MODULE_USUARIOS')")
    public ResponseEntity<Object> getAllUsers() {
        // List<UserRepository.getUser> users = userService.getAllUsers();
        log.info("Obteniendo todos los usuarios (simulado)");

        Response successResponse = Response.builder()
                .success(true)
                .message("Usuarios obtenidos exitosamente")
                .data("plots obtenidos exitosamente (simuladoooo)") // Aquí iría la lista real de usuarios
                .build();
        return ResponseEntity.ok(successResponse);
    }
}
