package com.agrosys.web.dto.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    private Integer userId; // Para identificar al usuario a actualizar, si es una actualización
    
    @NotBlank(message = "El nombre es requerido")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String firstName;

    @NotBlank(message = "El apellido es requerido")
    @Size(min = 3, max = 100, message = "El apellido debe tener entre 3 y 100 caracteres")
    private String lastName;

    @NotBlank(message = "El nombre de usuario es requerido")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    private String userName;

    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 6, max = 20, message = "La contraseña debe tener entre 6 y 20 caracteres")
    private String password;

    @NotNull(message = "El rol es requerido")
    @Min(value = 1, message = "El rol debe ser mayor a 0")
    private Integer rolId;
}