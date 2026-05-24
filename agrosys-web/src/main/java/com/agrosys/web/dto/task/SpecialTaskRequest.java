package com.agrosys.web.dto.task;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class SpecialTaskRequest {
    private String name;
    private BigDecimal paymentAmount;
    private LocalDate createAt;
    private Integer taskStageId;
    private LocalDate endAt;
    private String description;
    private List<Integer> workerIds;
}
