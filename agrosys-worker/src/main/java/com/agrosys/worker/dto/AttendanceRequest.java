package com.agrosys.worker.dto;

import lombok.Data;
import java.time.LocalDate;

@Data // Esto genera getters y setters automáticamente
public class AttendanceRequest {
    private Integer workerId;
    private LocalDate date;
    private Boolean attended;
    private Integer hoursWorked;
}