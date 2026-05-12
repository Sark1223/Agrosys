package com.agrosys.worker.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WorkerResponse {
    private Integer workerId;
    private String name;
    private String notas;
    private BigDecimal salary;
    private String photo;
    private String photoPublicId;
    private BigDecimal hourlyPay;
    private String message;
    private boolean success;
}