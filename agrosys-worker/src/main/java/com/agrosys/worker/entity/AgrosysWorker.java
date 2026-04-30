package com.agrosys.worker.entity;

import java.math.BigDecimal;

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
@Table(name = "WORKER", catalog = "agrosys_worker")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgrosysWorker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workerId")
    private int workerId;

    @Column(name = "name", unique = true, nullable = false, length = 50)
    private String name;

    @Column(name = "notas", length = 255)
    private String notas;

    @Column(name = "salary", nullable = false, precision = 10, scale = 2)
    private BigDecimal salary;

    @Column(name = "photo", columnDefinition = "LONGTEXT")
    private String photo;

    @Column(name = "photo_public_id", length = 255)
    private String photoPublicId;
}