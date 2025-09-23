package com.innowise.PaymentService.service;

import com.innowise.PaymentService.dto.PaymentRequestDto;
import com.innowise.PaymentService.dto.PaymentResponseDto;
import com.innowise.PaymentService.dto.SumResult;
import com.innowise.PaymentService.entity.Payment;
import com.innowise.PaymentService.mapper.PaymentMapper;
import com.innowise.PaymentService.repository.PaymentRepository;
import com.innowise.common.exception.ResourceNotFoundCustomException;
import com.innowise.common.exception.ExternalApiResponseCustomException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final AbsoluteRandomNumber absoluteRandomNumber;

    public PaymentResponseDto createPayment(PaymentRequestDto dto) {
        Payment payment = paymentMapper.toEntity(dto);

        setStatus(payment);
        payment.setTimestamp(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);
        return paymentMapper.toDto(savedPayment);
    }

    public List<PaymentResponseDto> getPaymentsByOrderId(Long orderId) {
        List<Payment> payments = paymentRepository.findAllByOrderId(orderId);

        if (payments.isEmpty()) {
            throw new ResourceNotFoundCustomException("Payments for order " + orderId + " not found");
        }

        return payments.stream()
                .map(payment -> paymentMapper.toDto(payment))
                .toList();
    }

    public List<PaymentResponseDto> getPaymentsByUserId(Long userId) {
        List<Payment> payments = paymentRepository.findAllByUserId(userId);

        if (payments.isEmpty()) {
            throw new ResourceNotFoundCustomException("Payments for user " + userId + " not found");
        }

        return payments.stream()
                .map(payment -> paymentMapper.toDto(payment))
                .toList();
    }

    public List<PaymentResponseDto> getPaymentsByStatus(String status) {
        List<Payment> payments = paymentRepository.findAllByStatus(status);

        if (payments.isEmpty()) {
            throw new ResourceNotFoundCustomException("Payments with status " + status + " not found");
        }
        return payments.stream()
                .map(payment -> paymentMapper.toDto(payment))
                .toList();
    }

    public BigDecimal getTotalSumOfPaymentsForDatePeriod(LocalDateTime startDate, LocalDateTime endDate) {
        SumResult result = paymentRepository.getTotalSumOfPaymentsForDatePeriod(startDate, endDate);
        return result != null ? result.getTotalSum() : BigDecimal.ZERO;
    }

    public void setStatus(Payment payment) {
        if (absoluteRandomNumber.getAbsoluteRandomNumber() % 2 == 0) {
            payment.setStatus("SUCCESS");
        } else {
            payment.setStatus("FAILED");
        }
    }
}
