package com.agrosys.web.dto.transaction;

import java.util.List;

import lombok.Data;

@Data
public class CreatePaymentRequest {
    private Integer weekId;
    private List<PaymentItem> payments;
}
