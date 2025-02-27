package edu.jakub.jureczko.storage.service;

import edu.jakub.jureczko.storage.config.BookingMessage;

import java.time.Duration;
import java.util.UUID;

public interface BookingService {
    boolean book(BookingMessage bookingMessage);

    boolean extend(UUID orderId, Duration extendBy);
}
