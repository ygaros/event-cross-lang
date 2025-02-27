package edu.jakub.jureczko.storage.model;

import lombok.Data;

@Data
public class Address {

    private String city;
    private String zipCode;
    private String country;
    private String street;

}
