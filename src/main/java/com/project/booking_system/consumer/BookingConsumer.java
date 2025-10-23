package com.project.booking_system.consumer;

import com.project.booking_system.config.KafkaConfig;
import com.project.booking_system.repository.BookingRepository;
import com.project.booking_system.service.BookingService;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service @Log4j2
public class BookingConsumer {
    private final BookingRepository bookingRepo;
    private final BookingService bookingService;

    public BookingConsumer(BookingRepository bookingRepo,
                           BookingService bookingService){
        this.bookingRepo=bookingRepo;
        this.bookingService=bookingService;
    }

    @KafkaListener(topics= KafkaConfig.BOOKING_TOPIC,groupId="booking-group")
    public void consume(String message){
        log.info("receive kafka ={} ", message);
        Long bookingId = Long.parseLong(message);
        bookingRepo.findById(bookingId).ifPresent(b->{
            bookingService.processBooking(b);
        });
    }
}
