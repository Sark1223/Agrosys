package com.agrosys.task.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SpecialTaskResponse {
    private Integer specialTaskId;
    private String name;
    private BigDecimal paymentAmount;
    private LocalDate createAt;
    private Integer taskStageId;
    private LocalDate endAt;
    private String description;
    private List<Integer> workerIds;
}
