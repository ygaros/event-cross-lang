package edu.jakub.jureczko.storage.service.impl;

import edu.jakub.jureczko.storage.config.BookingMessage;
import edu.jakub.jureczko.storage.entity.BookingEntity;
import edu.jakub.jureczko.storage.entity.ProductEntity;
import edu.jakub.jureczko.storage.model.Product;
import edu.jakub.jureczko.storage.repository.ProductRepository;
import edu.jakub.jureczko.storage.service.BookingService;
import edu.jakub.jureczko.storage.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ProductRepository productRepository;

    @Override
    public boolean book(BookingMessage bookingMessage) {
        List<ProductEntity> entities = productRepository.findAllByNameIn(
                bookingMessage.getProducts().stream().map(Product::getName).toList());
        if(!bookingMessage.getProducts().stream()
                .allMatch(booking ->
                        entities.stream()
                                .anyMatch(entity ->
                                        entity.getName().equals(booking.getName())
                                                && entity.getQuantity() >= booking.getQuantity()))){
            log.info("Could not book this order!");
            return false;
        }
        entities.forEach(entity -> bookingMessage.getProducts().stream()
                .filter(product -> product.getName().equals(entity.getName()))
                .findFirst()
                .ifPresent(product -> {
            BookingEntity bookingEntity = new BookingEntity();
            bookingEntity.setId(UUID.randomUUID());
            bookingEntity.setProductEntity(entity);
            bookingEntity.setExpirationDate(LocalDateTime.now().plus(Duration.ofHours(2)));
            bookingEntity.setQuantity(product.getQuantity());
            bookingEntity.setOrderId(bookingMessage.getId());

            entity.setQuantity(entity.getQuantity() - product.getQuantity());
            bookingRepository.save(bookingEntity);
            productRepository.save(entity);
            log.info("New booking: "+ bookingEntity);
        }));
        log.info("All products are booked");
        return true;
    }

    @Override
    public boolean extend(UUID orderId, Duration extendBy) {
        return bookingRepository.findAllByOrderId(orderId).stream()
                .peek(booking -> {
                    log.info("Extending by "+ extendBy + " order: "+ orderId);
                    booking.setExpirationDate(booking.getExpirationDate().plus(extendBy));
                    bookingRepository.save(booking);
                    log.info("Booking extended");
                })
                .allMatch(booking -> booking.getExpirationDate().isAfter(LocalDateTime.now()));
    }
}
