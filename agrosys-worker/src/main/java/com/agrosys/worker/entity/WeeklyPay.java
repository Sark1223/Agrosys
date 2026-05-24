package com.agrosys.worker.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "weekly_pay", catalog = "agrosys_worker")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyPay {

    @EmbeddedId
    private WeeklyPayId id;

    @Column(name = "amount", precision = 18, scale = 2)
    private BigDecimal amount;
}
