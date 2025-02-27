package edu.jakub.jureczko.storage.config;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PaymentFinishedMessage {
    private UUID orderId;
}
