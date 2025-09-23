package com.innowise.PaymentService.controller;

import com.innowise.PaymentService.dto.PaymentRequestDto;
import com.innowise.PaymentService.dto.PaymentResponseDto;
import com.innowise.PaymentService.dto.date.DateRangeRequest;
import com.innowise.PaymentService.service.PaymentService;
import com.innowise.common.exception.InvalidDateRangeCustomException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@AllArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponseDto> createPayment(@Valid @RequestBody PaymentRequestDto paymentRequestDto) {
        return new ResponseEntity<>(paymentService.createPayment(paymentRequestDto), HttpStatus.CREATED);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByOrderId(@PathVariable Long orderId) {
        return new ResponseEntity<>(paymentService.getPaymentsByOrderId(orderId), HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByUserId(@PathVariable Long userId) {
        return new ResponseEntity<>(paymentService.getPaymentsByUserId(userId), HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByStatus(@PathVariable String status) {
        return new ResponseEntity<>(paymentService.getPaymentsByStatus(status), HttpStatus.OK);
    }

    @PostMapping("/total")
    public ResponseEntity<BigDecimal> getTotalSumOfPaymentsForDatePeriod(
            @RequestBody @Valid DateRangeRequest dateRange) {

        if (dateRange.getStartDate().isAfter(dateRange.getEndDate())) {
            throw new InvalidDateRangeCustomException("Start date must be before end date");
        }
        if (dateRange.getStartDate().isAfter(LocalDateTime.now())) {
            throw new InvalidDateRangeCustomException("Start date cannot be in the future");
        }

        return new ResponseEntity<>(
                paymentService.getTotalSumOfPaymentsForDatePeriod(
                        dateRange.getStartDate(), dateRange.getEndDate()), HttpStatus.OK);
    }
}