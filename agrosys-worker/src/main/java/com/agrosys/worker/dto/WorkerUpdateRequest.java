package com.agrosys.worker.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WorkerUpdateRequest extends WorkerRequest {

    @NotNull(message = "El ID del worker es obligatorio")
    private Integer workerId;
}