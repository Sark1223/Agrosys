package com.agrosys.worker.entity;

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
@Table(name = "week", catalog = "agrosys_worker")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Week {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "week_id")
    private int weekId;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "name", nullable = false, length = 20)
    private String name;
}
