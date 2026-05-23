package com.agrosys.transaction.dto;
import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
@Data
public class TransactionRequest {
    @NotBlank(message = "El tipo de transacción es requerido")
    private String transactionType;
    @NotNull(message = "La fecha es requerida")
    private LocalDate createAt;
    @NotNull(message = "El monto es requerido")
    private BigDecimal amount;
    private String description;
    private Integer plotId;
    private Integer lotTradeId;
}