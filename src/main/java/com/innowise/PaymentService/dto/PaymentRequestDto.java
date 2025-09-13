package com.innowise.PaymentService.dto;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import org.bson.types.Decimal128;

import java.math.BigDecimal;

@Data
public class PaymentRequestDto {

    @NotNull
    private Long orderId;

    @NotNull
    private Long userId;

    @PositiveOrZero
    private BigDecimal amount;

}
