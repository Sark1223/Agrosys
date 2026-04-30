package com.agrosys.web.dto.plot.plantation;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PlantatioStageRegister {
    @Positive(message = "El plantatioId debe ser un número positivo")
    @Min(value = 1, message = "El plantatioId debe ser mayor que cero")
    private Integer plantatioId;

    @Positive(message = "El stageId debe ser un número positivo")
    @Min(value = 1, message = "El stageId debe ser mayor que cero")
    private Integer stageId;

    @NotBlank(message = "La fecha de inicio es requerida")
    @Size(min = 10, max = 30, message = "La fecha de inicio debe tener entre 10 y 30 caracteres")
    private String startAt;

    @Size(max = 30, message = "La fecha de fin debe tener entre 10 y 30 caracteres")
    private String endAt;

    @Size(max = 500, message = "Las notas deben tener maximo 500 caracteres")
    private String notas;
}
