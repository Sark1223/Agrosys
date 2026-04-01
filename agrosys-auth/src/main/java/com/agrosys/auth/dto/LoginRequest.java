package com.agrosys.auth.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class LoginRequest {
    @NotBlank(message = "El nombre de usuario es requerido")
    private String userName;
    
    @NotBlank(message = "La contraseña es requerida")
    private String password;
}