package com.agrosys.task.entity;

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
@Table(name = "TASK", schema = "agrosys_task")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgrosysTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Integer taskId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = 256)
    private String description;

    @Column(name = "create_at")
    private LocalDate createAt;

    @Column(name = "end_at")
    private LocalDate endAt;

    @Column(name = "task_stage_id")
    private Integer taskStageId;  // FK a TASK_STAGE

    @Column(name = "plantation_id")
    private Integer plantationId; // FK a plantios
}