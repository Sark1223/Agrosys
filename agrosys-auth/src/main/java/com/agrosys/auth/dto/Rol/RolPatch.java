package com.agrosys.auth.dto.Rol;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RolPatch {
    @NotNull(message = "El rol es requerido")
    @Min(value = 2, message = "El rol debe ser mayor a 1")
    private Integer userId;

    @NotBlank(message = "El nombre es requerido")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
    private String name;

    @Size(max = 100, message = "La descripción debe tener maximo 100 caracteres")
    private String description;

    @NotEmpty(message = "La lista de módulos no puede estar vacía")
    private List<@NotNull @Min(1) Integer> modulos;
}
