package com.project.booking_system.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.booking_system.config.KafkaConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service @Slf4j
public class BookingPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public BookingPublisher(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishBooking(Long bookingId, int quantity) {
        Map<String, Object> bookingData = new HashMap<>();
        bookingData.put("bookingId", bookingId);
        bookingData.put("quantity", quantity);
        try {
            String jsonMessage = objectMapper.writeValueAsString(bookingData);
            kafkaTemplate.send(KafkaConfig.BOOKING_TOPIC, jsonMessage);
        } catch (Exception e) {
            log.error("fail to publish booking {} ", bookingData, e);
        }
    }
}
