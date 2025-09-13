package com.innowise.PaymentService.dto;

import lombok.Data;
import org.bson.types.Decimal128;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PaymentResponseDto {

    private String id;

    private Long orderId;

    private Long userId;

    private String status;

    private LocalDateTime timestamp;

    private BigDecimal amount;

}
