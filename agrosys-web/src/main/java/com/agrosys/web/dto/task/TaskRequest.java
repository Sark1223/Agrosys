package com.agrosys.web.dto.task;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    private String description;

    @NotNull(message = "La fecha de creación es obligatoria")
    private LocalDate createAt;

    private LocalDate endAt;

    private Integer taskStageId;

    @NotNull(message = "El ID del plantío es obligatorio")
    private Integer plantationId;
}