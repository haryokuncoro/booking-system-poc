package com.project.booking_system.controller;

import com.project.booking_system.entity.Booking;
import com.project.booking_system.service.BookingPublisher;
import com.project.booking_system.service.BookingService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/booking")
public class BookingController {
    private final BookingService bookingService;
    private final BookingPublisher bookingPublisher;


    public BookingController(BookingService bookingService,
                             BookingPublisher bookingPublisher){
        this.bookingService=bookingService;
        this.bookingPublisher=bookingPublisher;
    }

    @PostMapping("/{productId}")
    public String book(@PathVariable Long productId, @RequestParam int quantity){
        Booking booking = bookingService.createBooking(productId,quantity);
        bookingPublisher.publishBooking(booking.getId(), quantity);
        return "Booking queued! Your bookingId: "+booking.getId();
    }


}
