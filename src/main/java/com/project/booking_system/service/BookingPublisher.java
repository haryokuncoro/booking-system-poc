package com.project.booking_system.service;


import com.project.booking_system.config.KafkaConfig;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class BookingPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public BookingPublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishBooking(Long productId, int quantity) {
        String message = productId + ":" + quantity;
        kafkaTemplate.send(KafkaConfig.BOOKING_TOPIC, message);
    }
}
