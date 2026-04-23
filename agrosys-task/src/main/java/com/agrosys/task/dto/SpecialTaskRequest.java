package com.agrosys.task.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class SpecialTaskRequest {

    @NotNull(message = "El ID de la tarea es obligatorio")
    private Integer taskId;

    @NotNull(message = "El ID del trabajador es obligatorio")
    private Integer workerId;

    @Positive(message = "El monto de pago debe ser positivo")
    private BigDecimal paymentAmount;
}