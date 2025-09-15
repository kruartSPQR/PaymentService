package com.innowise.PaymentService.unitTests;

import com.innowise.PaymentService.dto.PaymentRequestDto;
import com.innowise.PaymentService.dto.PaymentResponseDto;
import com.innowise.PaymentService.dto.SumResult;
import com.innowise.PaymentService.entity.Payment;
import com.innowise.PaymentService.mapper.PaymentMapper;
import com.innowise.PaymentService.repository.PaymentRepository;
import com.innowise.PaymentService.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceUnitTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private WebClient webClient;

    @InjectMocks
    private PaymentService paymentService;

    private PaymentRequestDto paymentRequestDto;
    private Payment paymentEntity;
    private PaymentResponseDto paymentResponseDto;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private SumResult sumResult;

    @BeforeEach
    void setUp() {
        paymentRequestDto = new PaymentRequestDto();
        paymentRequestDto.setOrderId(1L);
        paymentRequestDto.setUserId(1L);
        paymentRequestDto.setAmount(BigDecimal.valueOf(100.0));

        paymentEntity = new Payment();
        paymentEntity.setId("1");
        paymentEntity.setOrderId(1L);
        paymentEntity.setUserId(1L);
        paymentEntity.setAmount(BigDecimal.valueOf(100.0));
        paymentEntity.setStatus("SUCCESS");
        paymentEntity.setTimestamp(LocalDateTime.now());

        paymentResponseDto = new PaymentResponseDto();
        paymentResponseDto.setId("1");
        paymentResponseDto.setOrderId(1L);
        paymentResponseDto.setUserId(1L);
        paymentResponseDto.setAmount(BigDecimal.valueOf(100.0));
        paymentResponseDto.setStatus("SUCCESS");
        paymentResponseDto.setTimestamp(paymentEntity.getTimestamp());

        startDate = LocalDateTime.of(2023, 1, 1, 0, 0);
        endDate = LocalDateTime.of(2023, 12, 31, 23, 59);

        sumResult = new SumResult();
        sumResult.setTotalSum(BigDecimal.valueOf(100.0));
    }

    @Test
    void testCreatePaymentSuccess() {
        mockWebClientResponse("2");

        when(paymentMapper.toEntity(paymentRequestDto)).thenReturn(paymentEntity);
        when(paymentRepository.save(any(Payment.class))).thenReturn(paymentEntity);
        when(paymentMapper.toDto(paymentEntity)).thenReturn(paymentResponseDto);

        PaymentResponseDto result = paymentService.createPayment(paymentRequestDto);

        assertNotNull(result);
        assertEquals("SUCCESS", result.getStatus());
        assertEquals(paymentResponseDto.getOrderId(), result.getOrderId());
        assertEquals(paymentResponseDto.getUserId(), result.getUserId());

        verify(paymentMapper).toEntity(paymentRequestDto);
        verify(paymentRepository).save(any(Payment.class));
        verify(paymentMapper).toDto(paymentEntity);
    }

    @Test
    void testCreatePaymentFailed() {
        mockWebClientResponse("7");

        when(paymentMapper.toEntity(paymentRequestDto)).thenReturn(paymentEntity);
        when(paymentRepository.save(any(Payment.class))).thenReturn(paymentEntity);
        when(paymentMapper.toDto(paymentEntity)).thenReturn(paymentResponseDto);

        PaymentResponseDto result = paymentService.createPayment(paymentRequestDto);

        assertNotNull(result);
        assertEquals("SUCCESS", result.getStatus());

        verify(paymentMapper).toEntity(paymentRequestDto);
        verify(paymentRepository).save(any(Payment.class));
        verify(paymentMapper).toDto(paymentEntity);
    }

    @Test
    void testCreatePaymentWithWebClientFailure() {
        mockWebClientResponse(null);

        when(paymentMapper.toEntity(paymentRequestDto)).thenReturn(paymentEntity);

        assertThrows(RuntimeException.class, () -> paymentService.createPayment(paymentRequestDto));
    }

    @Test
    void testGetPaymentsByOrderId() {
        List<Payment> payments = List.of(paymentEntity);
        when(paymentRepository.findAllByOrderId(1L)).thenReturn(payments);
        when(paymentMapper.toDto(paymentEntity)).thenReturn(paymentResponseDto);

        List<PaymentResponseDto> result = paymentService.getPaymentsByOrderId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(paymentResponseDto.getOrderId(), result.get(0).getOrderId());

        verify(paymentRepository).findAllByOrderId(1L);
        verify(paymentMapper).toDto(paymentEntity);
    }

    @Test
    void testGetPaymentsByUserId() {
        List<Payment> payments = List.of(paymentEntity);
        when(paymentRepository.findAllByUserId(1L)).thenReturn(payments);
        when(paymentMapper.toDto(paymentEntity)).thenReturn(paymentResponseDto);

        List<PaymentResponseDto> result = paymentService.getPaymentsByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(paymentResponseDto.getUserId(), result.get(0).getUserId());

        verify(paymentRepository).findAllByUserId(1L);
        verify(paymentMapper).toDto(paymentEntity);
    }

    @Test
    void testGetPaymentsByStatus() {
        List<Payment> payments = List.of(paymentEntity);
        when(paymentRepository.findAllByStatus("SUCCESS")).thenReturn(payments);
        when(paymentMapper.toDto(paymentEntity)).thenReturn(paymentResponseDto);

        List<PaymentResponseDto> result = paymentService.getPaymentsByStatus("SUCCESS");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(paymentResponseDto.getStatus(), result.get(0).getStatus());

        verify(paymentRepository).findAllByStatus("SUCCESS");
        verify(paymentMapper).toDto(paymentEntity);
    }

    @Test
    void testGetTotalSumOfPaymentsForDatePeriod() {
        when(paymentRepository.getTotalSumOfPaymentsForDatePeriod(startDate, endDate)).thenReturn(sumResult);

        BigDecimal result = paymentService.getTotalSumOfPaymentsForDatePeriod(startDate, endDate);

        assertEquals(BigDecimal.valueOf(100.0), result);

        verify(paymentRepository).getTotalSumOfPaymentsForDatePeriod(startDate, endDate);
    }

    @Test
    void testGetTotalSumOfPaymentsForDatePeriodZero() {
        when(paymentRepository.getTotalSumOfPaymentsForDatePeriod(startDate, endDate)).thenReturn(null);

        BigDecimal result = paymentService.getTotalSumOfPaymentsForDatePeriod(startDate, endDate);

        assertEquals(BigDecimal.ZERO, result);

        verify(paymentRepository).getTotalSumOfPaymentsForDatePeriod(startDate, endDate);
    }

    private void mockWebClientResponse(String response) {
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headerSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        Mono<String> mono = response != null ? Mono.just(response) : Mono.empty();

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(headerSpec);
        when(headerSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(mono);
    }
}