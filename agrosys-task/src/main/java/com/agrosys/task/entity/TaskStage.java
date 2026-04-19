package com.agrosys.task.entity;

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
@Table(name = "TASK_STAGE", schema = "agrosys_task")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskStage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_stage_id")
    private Integer taskStageId;

    @Column(name = "description", length = 30)
    private String description;
}