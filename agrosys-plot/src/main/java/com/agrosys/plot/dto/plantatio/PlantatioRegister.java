package com.agrosys.plot.dto.plantatio;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PlantatioRegister {
    @NotBlank(message = "El nombre es requerido")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
    private String name;

    // @NotBlank(message = "La fecha de inicio es requerida")
    // @Size(min = 10, max = 30, message = "La fecha de inicio debe tener entre 10 y 30 caracteres")
    // private String startAt;

    // @Size(max = 30, message = "La fecha de fin debe tener entre 10 y 30 caracteres")
    // private String endAt;

    @Size(max = 500, message = "Las notas deben tener maximo 500 caracteres")
    private String notas;

    @Positive(message = "El plotId debe ser un número positivo")
    @Min(value = 1, message = "El plotId debe ser mayor que cero")
    private Integer plotId;
}
