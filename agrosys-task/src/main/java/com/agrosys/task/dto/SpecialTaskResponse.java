package com.agrosys.task.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SpecialTaskResponse {
    private Integer taskId;
    private Integer workerId;
    private BigDecimal paymentAmount;
    private Integer taskStageId;
}