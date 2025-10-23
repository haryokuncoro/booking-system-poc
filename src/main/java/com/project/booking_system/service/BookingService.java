package com.project.booking_system.service;

import com.project.booking_system.entity.Booking;
import com.project.booking_system.entity.Product;
import com.project.booking_system.repository.BookingRepository;
import com.project.booking_system.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BookingService {
    private final ProductRepository productRepo;
    private final BookingRepository bookingRepo;

    public BookingService(ProductRepository productRepo, BookingRepository bookingRepo) {
        this.productRepo = productRepo;
        this.bookingRepo = bookingRepo;
    }

    public Booking createBooking(Long productId, int qty) {
        Booking booking = new Booking();
        booking.setProductId(productId);
        booking.setQuantity(qty);
        booking.setStatus(Booking.Status.QUEUED);
        booking.setCreatedAt(LocalDateTime.now());
        return bookingRepo.save(booking);
    }

    @Transactional
    public void processBooking(Booking booking) {
        Product p = productRepo.findByIdForUpdate(booking.getProductId());
        if (p==null || p.getStock()<booking.getQuantity()) {
            booking.setStatus(Booking.Status.FAILED);
        } else {
            p.setStock(p.getStock()-booking.getQuantity());
            productRepo.save(p);
            booking.setStatus(Booking.Status.SUCCESS);
        }
        bookingRepo.save(booking);
    }

    public Booking.Status getStatus(Long bookingId) {
        return bookingRepo.findById(bookingId).map(Booking::getStatus).orElse(null);
    }
}
