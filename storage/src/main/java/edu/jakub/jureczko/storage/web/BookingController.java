package edu.jakub.jureczko.storage.web;


import edu.jakub.jureczko.storage.config.BookingMessage;
import edu.jakub.jureczko.storage.config.BookingPaymentMessage;
import edu.jakub.jureczko.storage.config.PaymentFinishedMessage;
import edu.jakub.jureczko.storage.config.PaymentStatus;
import edu.jakub.jureczko.storage.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;
    private final KafkaTemplate<UUID, PaymentFinishedMessage> kafkaTemplate;

    @KafkaListener(
            id = "booking-listener",
            topics = "book.products.topic",
            clientIdPrefix = "booking-service",
            containerFactory = "bookContainerFactory")
    public void bookProducts(BookingMessage bookingMessage, Acknowledgment acknowledgment) {
        log.info(bookingMessage.toString());
        if (bookingService.book(bookingMessage)) {
            acknowledgment.acknowledge();
        }
        acknowledgment.acknowledge();
    }

    @KafkaListener(
            id = "booking-payments-listener",
            topics = "book.products.payment.topic",
            clientIdPrefix = "booking-service",
            containerFactory = "bookPaymentContainerFactory")
    public void bookProductsPayment(BookingPaymentMessage bookingPaymentMessage, Acknowledgment acknowledgment) {
        log.info(bookingPaymentMessage.toString());
        if (bookingService.extend(bookingPaymentMessage.getOrderId(), bookingPaymentMessage.getStatus().getDuration())) {
            if(bookingPaymentMessage.getStatus().equals(PaymentStatus.FINISHED)){
                PaymentFinishedMessage message = new PaymentFinishedMessage(bookingPaymentMessage.getOrderId());
                log.info("Sending: " + message);
                kafkaTemplate.send("deliver.topic", message);
            }
            acknowledgment.acknowledge();
        }
    }
}
