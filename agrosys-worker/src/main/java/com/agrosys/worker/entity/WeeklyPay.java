package com.agrosys.worker.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "weekly_pay", catalog = "agrosys_worker")
@IdClass(WeeklyPayId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyPay {

    @Id
    @ManyToOne
    @JoinColumn(name = "worker_id", referencedColumnName = "workerId")
    private AgrosysWorker worker;

    @Id
    @ManyToOne
    @JoinColumn(name = "week_id", referencedColumnName = "week_id")
    private Week week;

    @Column(name = "amount", precision = 10, scale = 2)
    private BigDecimal amount;
}
