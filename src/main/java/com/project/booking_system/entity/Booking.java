package com.project.booking_system.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.Data;

import java.time.LocalDateTime;

@Entity @Data
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    private int quantity;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime createdAt;

    @Version
    private Long version; // optimistic lock

    public enum Status { QUEUED, SUCCESS, FAILED }
}

