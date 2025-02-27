package edu.jakub.jureczko.storage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConsumerEntity {
    @Id
    private UUID id;

    private String firstName;
    private String lastName;


    @Embedded
    private AddressEntity address;
}
