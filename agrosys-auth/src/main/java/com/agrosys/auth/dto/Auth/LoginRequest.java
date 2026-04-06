package com.agrosys.auth.dto.Auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "El nombre de usuario es requerido")
    private String userName;
    
    @NotBlank(message = "La contraseña es requerida")
    private String password;
}