package com.agrosys.worker.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkerResponse {
    private Integer workerId;
    private String name;
    private String notas;
    private String message;
    private boolean success;
}