package com.agrosys.task.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskStageResponse {
    private Integer taskStageId;
    private String description;
}