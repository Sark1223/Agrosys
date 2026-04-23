package com.agrosys.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TaskStageRequest {

    @NotBlank(message = "La descripción de la etapa es obligatoria")
    @Size(max = 30, message = "La descripción no puede superar 30 caracteres")
    private String description;
}