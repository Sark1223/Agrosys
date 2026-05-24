package com.agrosys.task.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SpecialTaskRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    private String name;

    @NotNull(message = "El monto de pago es obligatorio")
    @Positive(message = "El monto de pago debe ser positivo")
    private BigDecimal paymentAmount;

    @NotNull(message = "La fecha de creación es obligatoria")
    private LocalDate createAt;

    private Integer taskStageId;

    private LocalDate endAt;

    @Size(max = 256)
    private String description;

    private List<Integer> workerIds;
}
