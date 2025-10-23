package com.project.booking_system.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.booking_system.config.KafkaConfig;
import com.project.booking_system.entity.Booking;
import com.project.booking_system.repository.BookingRepository;
import com.project.booking_system.service.BookingNotificationService;
import com.project.booking_system.service.BookingService;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service @Log4j2
public class BookingConsumer {
    private final BookingRepository bookingRepo;
    private final BookingService bookingService;
    private final BookingNotificationService notificationService;

    private final ObjectMapper objectMapper;

    public BookingConsumer(BookingRepository bookingRepo,
                           BookingService bookingService, BookingNotificationService notificationService, ObjectMapper objectMapper){
        this.bookingRepo=bookingRepo;
        this.bookingService=bookingService;
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics= KafkaConfig.BOOKING_TOPIC,groupId="booking-group")
    public void consume(String jsonMessage) {
        log.info("Receive JSON Kafka: {}", jsonMessage);
        try {
            Map<String, Object> data = objectMapper.readValue(jsonMessage, new TypeReference<Map<String, Object>>() {});
            Long bookingId = ((Number) data.get("bookingId")).longValue();
            Booking booking = bookingRepo.findById(bookingId).orElse(null);
            if( Objects.nonNull(booking)){
                bookingService.processBooking(booking);
                notificationService.sendStatus(bookingId, booking.getStatus().name());
            }else {
                log.warn("fail to retrieve booking while process booking, data={}", jsonMessage);
            }

        } catch (Exception e) {
            log.error("fail to consume kafka and process booking ={}", jsonMessage, e);
        }
    }
}
