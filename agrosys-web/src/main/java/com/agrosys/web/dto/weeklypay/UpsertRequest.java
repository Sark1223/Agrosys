package com.agrosys.web.dto.weeklypay;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class UpsertRequest {
    private Integer workerId;
    private String weekId;
    private BigDecimal amount;
}
