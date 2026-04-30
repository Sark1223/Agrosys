package com.agrosys.task.dto;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskResponse {
    private Integer taskId;
    private String name;
    private String description;
    private LocalDate createAt;
    private LocalDate endAt;
    private Integer taskStageId;
    private Integer plantationId;
}