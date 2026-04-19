package com.agrosys.task.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TaskRequest {

    @NotBlank(message = "El nombre de la tarea es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String name;

    @Size(max = 256, message = "La descripción no puede superar 256 caracteres")
    private String description;

    private LocalDate createAt;

    private LocalDate endAt;

    private Integer taskStageId;

    private Integer plantationId;
}