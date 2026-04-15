package com.agrosys.auth.dto.User;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserPatch {
    @NotNull(message = "El usuario es requerido")
    @Min(value = 2, message = "El usuario debe ser mayor a 1")
    private Integer userId;

    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 6, max = 20, message = "La contraseña debe tener entre 6 y 20 caracteres")
    private String password;
}
