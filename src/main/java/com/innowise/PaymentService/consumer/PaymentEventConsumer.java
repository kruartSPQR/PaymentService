package com.innowise.PaymentService.consumer;

import com.innowise.PaymentService.dto.PaymentRequestDto;
import com.innowise.PaymentService.dto.PaymentResponseDto;
import com.innowise.PaymentService.producer.PaymentProducer;
import com.innowise.PaymentService.service.PaymentService;
import com.innowise.common.event.OrderCreatedEvent;
import com.innowise.common.event.PaymentCreatedEvent;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@AllArgsConstructor
public class PaymentEventConsumer {

    PaymentService paymentService;
    PaymentProducer paymentProducer;

    @KafkaListener(topics = "create-order-topic", groupId = "payment-group")
    public void handleOrderCreated(OrderCreatedEvent event, Acknowledgment ack) {
        PaymentRequestDto dto = new PaymentRequestDto();
        dto.setOrderId(event.getOrderId());
        dto.setUserId(event.getUserId());
        dto.setAmount(event.getAmount());

        PaymentResponseDto response = paymentService.createPayment(dto);
        PaymentCreatedEvent paymentEvent = new PaymentCreatedEvent(
                response.getId(), event.getOrderId(), response.getAmount(),
                response.getStatus(), LocalDateTime.now()
        );
        paymentProducer.sendPaymentCreated(paymentEvent);

        ack.acknowledge();
    }
}
