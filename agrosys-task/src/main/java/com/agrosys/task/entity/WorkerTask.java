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
@Table(name = "WORKER_TASK", catalog ="agrosys_task")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkerTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "worker_task_id")
    private Integer workerTaskId;

    @Column(name="worker_id", nullable=false)
    private Integer workerId;

    @Column(name="special_task_id", nullable=false)
    private Integer specialTaskId;

    
    
}
