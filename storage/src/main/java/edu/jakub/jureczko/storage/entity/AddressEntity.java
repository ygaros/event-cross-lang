package edu.jakub.jureczko.storage.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddressEntity {

    private String city;
    private String zipCode;
    private String country;
    private String street;

    private String phone;
}
