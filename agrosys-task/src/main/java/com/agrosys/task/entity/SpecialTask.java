package com.agrosys.task.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "SPECIAL_TASK", schema = "agrosys_task")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecialTask {

    @Id
    @Column(name = "task_id")
    private Integer taskId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "task_id")
    private AgrosysTask task;

    @Column(name = "worker_id", nullable = false)
    private Integer workerId;

    @Column(name = "payment_amount", precision = 18, scale = 2)
    private BigDecimal paymentAmount;
}