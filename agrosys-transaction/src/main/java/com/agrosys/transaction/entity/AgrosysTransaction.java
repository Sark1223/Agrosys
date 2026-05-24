package com.agrosys.transaction.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transactions", catalog = "agrosys_db")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgrosysTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Integer transactionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 50)
    private AgrosysTransactionType transactionType;

    @Column(name = "create_at", nullable = false)
    private LocalDate createAt;

    @Column(name = "amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "description", length = 256)
    private String description;

    @Column(name = "plot_id")
    private Integer plotId;

    @Column(name = "lot_trade_id")
    private Integer lotTradeId;
}
