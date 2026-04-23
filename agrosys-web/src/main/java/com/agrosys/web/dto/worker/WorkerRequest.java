package com.agrosys.web.dto.worker;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class WorkerRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede superar 50 caracteres")
    private String name;

    @Size(max = 255, message = "Las notas no pueden superar 255 caracteres")
    private String notas;

    @NotNull(message = "El salario es obligatorio")
    @DecimalMin(value = "0.00", message = "El salario no puede ser negativo")
    private BigDecimal salary;

    private String photo;
}