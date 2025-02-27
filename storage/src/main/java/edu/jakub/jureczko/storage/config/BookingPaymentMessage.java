package edu.jakub.jureczko.storage.config;

import edu.jakub.jureczko.storage.model.Address;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class BookingPaymentMessage {
    private UUID orderId;
    private PaymentStatus status;
    private String type;
}
