package edu.jakub.jureczko.storage.model;

import lombok.Data;

@Data
public class Product {
    private String name;
    private Integer price;
    private Integer quantity;
}
