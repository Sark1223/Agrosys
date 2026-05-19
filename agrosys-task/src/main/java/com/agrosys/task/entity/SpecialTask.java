package com.agrosys.task.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "SPECIAL_TASK", catalog = "agrosys_task")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecialTask {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "special_task_id")
    private Integer specialTaskId;  

    @Column(name = "name", nullable = false, length=100)
    private String name;

    @Column(name = "payment_amount", precision = 18, scale = 2, nullable=false)
    private BigDecimal paymentAmount;
    
    @Column(name = "create_at", nullable = false)
    private LocalDate createAt;
    
    @Column(name = "task_stage_id", nullable = false)
    private Integer taskStageId;

    @Column(name = "end_at")
    private LocalDate endAt;

    @Column(name = "description", length= 256)
    private String description;

}