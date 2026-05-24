package com.agrosys.transaction.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PaymentItem {
    private Integer workerId;
    private BigDecimal amount;
}
