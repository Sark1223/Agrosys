package com.agrosys.web.dto.plot.lot;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LotRegister {
    @NotBlank(message = "El nombre es requerido")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String name;

    @Size(max = 255, message = "Las notas deben tener maximo 255 caracteres")
    private String description;

    @Positive(message = "Es necesario elegir una plantación.")
    @Min(value = 1)
    private Integer plantationId;
}
