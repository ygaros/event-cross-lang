package edu.jakub.jureczko.storage.repository;

import edu.jakub.jureczko.storage.entity.BookingEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends CrudRepository<BookingEntity, UUID> {
    List<BookingEntity> findAllByOrderId(UUID orderId);
}
