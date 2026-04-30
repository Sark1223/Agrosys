package com.agrosys.task.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TaskRequest {

    @NotBlank(message = "El nombre de la tarea es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String name;

    @Size(max = 256, message = "La descripción no puede superar 256 caracteres")
    private String description;

    @NotNull(message = "La fecha de creación es obligatoria")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createAt;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endAt;

    private Integer taskStageId;

    @NotNull(message = "El ID del plantío es obligatorio")
    private Integer plantationId;
}