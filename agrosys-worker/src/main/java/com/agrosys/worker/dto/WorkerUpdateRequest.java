package com.agrosys.worker.dto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WorkerUpdateRequest extends WorkerRequest {

    @NotNull(message = "El ID del worker es obligatorio")
    private Integer workerId;
}