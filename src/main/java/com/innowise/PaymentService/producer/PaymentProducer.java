package com.innowise.PaymentService.producer;

import com.innowise.common.event.PaymentCreatedEvent;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PaymentProducer {
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendPaymentCreated(PaymentCreatedEvent event) {
        String key = event.getOrderId().toString();
        kafkaTemplate.send("payment-created-topic", key, event);
    }
}
