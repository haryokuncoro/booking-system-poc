package com.project.booking_system.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BookingNotificationService {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter createEmitter(Long bookingId) {
        SseEmitter emitter = new SseEmitter(30_000L); // timeout 30 detik
        emitters.put(bookingId, emitter);

        emitter.onCompletion(() -> emitters.remove(bookingId));
        emitter.onTimeout(() -> emitters.remove(bookingId));

        return emitter;
    }

    public void sendStatus(Long bookingId, String status) {
        SseEmitter emitter = emitters.get(bookingId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("booking-status").data(status));
                emitter.complete();
            } catch (IOException e) {
                emitters.remove(bookingId);
            }
        }
    }
}
