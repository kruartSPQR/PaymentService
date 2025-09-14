package com.innowise.PaymentService.integrationTests;

import com.innowise.PaymentService.consumer.PaymentEventConsumer;
import com.innowise.PaymentService.dto.PaymentRequestDto;
import com.innowise.PaymentService.dto.PaymentResponseDto;
import com.innowise.PaymentService.producer.PaymentProducer;
import com.innowise.PaymentService.repository.PaymentRepository;
import com.innowise.PaymentService.service.PaymentService;
import com.innowise.common.exception.ResourceNotFoundCustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class PaymentServiceIntegrationTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @MockitoBean
    private PaymentProducer paymentProducer;

    @MockitoBean
    private PaymentEventConsumer paymentEventConsumer;

    @MockitoBean
    private WebClient webClient;

    @MockitoBean
    private WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;

    @MockitoBean
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;

    @MockitoBean
    private WebClient.ResponseSpec responseSpec;

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() {
        paymentRepository.deleteAll();

        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = Mockito.mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = Mockito.mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = Mockito.mock(WebClient.ResponseSpec.class);

        // Now set up the mock chain
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("2"));
    }

    private PaymentRequestDto createTestPaymentDto() {
        PaymentRequestDto dto = new PaymentRequestDto();
        dto.setOrderId(1L);
        dto.setUserId(1L);
        dto.setAmount(BigDecimal.valueOf(100.0));
        return dto;
    }

    @Test
    void shouldGetPaymentsByOrderId() {
        PaymentRequestDto request1 = createTestPaymentDto();
        PaymentRequestDto request2 = createTestPaymentDto();
        request2.setOrderId(2L);

        paymentService.createPayment(request1);
        paymentService.createPayment(request2);

        List<PaymentResponseDto> payments = paymentService.getPaymentsByOrderId(1L);

        assertEquals(1, payments.size());
        assertEquals(1L, payments.get(0).getOrderId());
    }

    @Test
    void shouldGetPaymentsByUserId() {
        PaymentRequestDto request1 = createTestPaymentDto();
        PaymentRequestDto request2 = createTestPaymentDto();
        request2.setUserId(2L);

        paymentService.createPayment(request1);
        paymentService.createPayment(request2);

        List<PaymentResponseDto> payments = paymentService.getPaymentsByUserId(1L);

        assertEquals(1, payments.size());
        assertEquals(1L, payments.get(0).getUserId());
    }

    @Test
    @DirtiesContext
    void shouldCreatePaymentWithFailedStatusAndHandleNotFound() {

        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = Mockito.mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = Mockito.mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = Mockito.mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("3"));

        PaymentRequestDto request = createTestPaymentDto();
        PaymentResponseDto response = paymentService.createPayment(request);

        assertNotNull(response.getId());
        assertEquals("FAILED", response.getStatus());

        assertThrows(ResourceNotFoundCustomException.class, () -> {
            paymentService.getPaymentsByStatus("PENDING");
        });
    }
    @Test
    @DirtiesContext
    void shouldCreatePaymentWithSuccessStatus() {

        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = Mockito.mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = Mockito.mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = Mockito.mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("2"));

        PaymentRequestDto request = createTestPaymentDto();
        PaymentResponseDto response = paymentService.createPayment(request);

        assertNotNull(response.getId());
        assertEquals("SUCCESS", response.getStatus());
    }

    @Test
    void shouldGetTotalSumOfPaymentsForDatePeriod() {
        PaymentRequestDto request1 = createTestPaymentDto();
        PaymentRequestDto request2 = createTestPaymentDto();
        request2.setAmount(BigDecimal.valueOf(200.0));

        paymentService.createPayment(request1);
        paymentService.createPayment(request2);

        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        BigDecimal total = paymentService.getTotalSumOfPaymentsForDatePeriod(start, end);

        assertEquals(0, total.compareTo(BigDecimal.valueOf(300.0)));
    }
}