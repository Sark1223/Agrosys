package com.agrosys.transaction.dto;

import java.util.List;

import lombok.Data;

@Data
public class CreatePaymentRequest {
    private Integer weekId;
    private List<PaymentItem> payments;
}
