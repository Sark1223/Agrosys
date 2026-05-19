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
    @Positive(message = "El monto debe ser mayor a cero")
    private BigDecimal amount;
    @NotBlank(message = "La descripción es requerida")
    private String description;
    private Integer lotTradeId;
}