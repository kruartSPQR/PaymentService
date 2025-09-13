package com.innowise.PaymentService.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Document(collection = "payments")
public class Payment {

    @Id
    private String id ;
    @Indexed
    private Long orderId;
    @Indexed
    private Long userId;

    private String status = "PENDING";
    @Indexed
    private LocalDateTime timestamp;

    private BigDecimal amount;

}
