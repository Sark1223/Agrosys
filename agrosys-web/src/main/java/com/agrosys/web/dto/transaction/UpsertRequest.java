package com.agrosys.web.dto.transaction;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class UpsertRequest {
    private Integer workerId;
    private Integer weekId;
    private BigDecimal amount;
}
