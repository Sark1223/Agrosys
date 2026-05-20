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
@Table(name = "LOT")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgrosysLot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lotId")
    private Integer lotId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = true, length = 255)
    private String description;

    @Column(name = "plantationId", nullable = false)
    private Integer plantationId;
}
