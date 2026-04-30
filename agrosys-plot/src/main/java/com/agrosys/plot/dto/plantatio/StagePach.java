package com.agrosys.plot.dto.plantatio;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StagePach {

    @Positive(message = "El Id debe ser un número positivo")
    private Integer id;

    @Positive(message = "El estado debe ser un número positivo")
    private Integer stageId;

    @NotBlank(message = "La fecha de finalización es requerida")
    @Size(min = 10, max = 30, message = "La fecha de finalización debe tener entre 10 y 30 caracteres")
    private String endAt;
}
