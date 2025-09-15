package com.innowise.PaymentService.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SumResult {
    private BigDecimal totalSum;

    public BigDecimal getTotalSum() {
        return totalSum;
    }
}