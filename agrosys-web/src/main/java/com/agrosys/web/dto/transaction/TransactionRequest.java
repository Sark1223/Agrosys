package com.agrosys.web.dto.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class TransactionRequest {
    private String transactionType;
    private LocalDate createAt;
    private BigDecimal amount;
    private String description;
    private Integer plotId;
    private Integer lotTradeId;

}
