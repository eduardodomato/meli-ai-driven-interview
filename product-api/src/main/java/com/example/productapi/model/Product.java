package com.example.productapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Product entity representing a product in the system
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private String imageUrl;
    private Integer rating;
    private Map<String, String> specifications;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Product(String name, String description, BigDecimal price, String category, String imageUrl, Integer rating, Map<String, String> specifications) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.specifications = specifications;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
