package edu.jakub.jureczko.storage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingEntity {
    @Id
    private UUID id;

    private UUID orderId;

    private Integer quantity;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private ProductEntity productEntity;

    private LocalDateTime expirationDate;

    @Override
    public String toString() {
        return "BookingEntity{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", quantity=" + quantity +
                ", productEntity=" + productEntity +
                ", expirationDate=" + expirationDate +
                '}';
    }
}
