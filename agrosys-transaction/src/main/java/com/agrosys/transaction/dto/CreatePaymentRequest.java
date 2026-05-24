package com.agrosys.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class CreatePaymentRequest {
    private String weekId;
    private LocalDate endDate;
    private List<PaymentItem> payments;

    @Data
    public static class PaymentItem {
        private String workerName;
        private BigDecimal weeklyPay;
        private BigDecimal extras;
        private BigDecimal total;
    }
}
