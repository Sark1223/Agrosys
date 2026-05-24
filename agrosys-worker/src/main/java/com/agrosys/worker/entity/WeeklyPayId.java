package com.agrosys.worker.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyPayId implements Serializable {

    @Column(name = "worker_id")
    private Integer workerId;

    @Column(name = "week_id", length = 10)
    private String weekId;
}
