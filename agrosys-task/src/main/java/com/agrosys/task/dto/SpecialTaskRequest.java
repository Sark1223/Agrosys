package com.agrosys.task.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SpecialTaskRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String name;

    @NotNull(message = "El monto de pago es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto de pago debe ser mayor a cero")
    private BigDecimal paymentAmount;

    @NotNull(message = "La fecha de creación es obligatoria")
    private LocalDate createAt;

    @NotNull(message = "El estado de la tarea es obligatorio")
    private Integer taskStageId;

    private LocalDate endAt;

    @Size(max = 256, message = "La descripción no puede superar 256 caracteres")
    private String description;

    private List<Integer> workerIds;
}
