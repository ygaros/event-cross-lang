package edu.jakub.jureczko.storage.config;

import edu.jakub.jureczko.storage.model.Address;
import edu.jakub.jureczko.storage.model.Customer;
import edu.jakub.jureczko.storage.model.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class BookingMessage {
    private UUID id;
    private List<Product> products;
    private Customer customer;
    private Address address;

}
