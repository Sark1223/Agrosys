package com.agrosys.task.dto;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private Integer taskId;
    private String name;
    private String description;
    private LocalDate createAt;
    private LocalDate endAt;
    private Integer taskStageId;
    private Integer plantationId;
}