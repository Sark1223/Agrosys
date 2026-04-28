package com.agrosys.plot.entity;

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
@Table(name = "plantatio")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgrosysPlantatio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plantatioId")
    private Integer plantatioId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "startAt", nullable = false)
    private String startAt;

    @Column(name = "endAt", nullable = false)
    private String endAt;

    @Column(name = "notas", nullable = true, length = 255)
    private String notas;

    @Column(name = "plotId", nullable = false)
    private Integer plotId;
}
