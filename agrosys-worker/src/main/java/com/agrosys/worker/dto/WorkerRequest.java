package com.agrosys.worker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class WorkerRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede superar 50 caracteres")
    private String name;

    @NotBlank(message = "Las notas son obligatorias")
    @Size(max = 255, message = "Las notas no pueden superar 255 caracteres")
    private String notas;
}