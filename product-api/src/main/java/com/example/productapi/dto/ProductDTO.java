package com.example.productapi.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Data Transfer Object for Product operations
 * Excludes internal fields like createdAt and updatedAt from API responses
 * 
 * Using record for immutability, conciseness, and clear data carrier intent
 */
public record ProductDTO(
    Long id,
    
    @NotBlank(message = "Product name is required")
    @Size(max = 255, message = "Product name must not exceed 255 characters")
    String name,
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    String description,
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Price must have at most 10 integer digits and 2 decimal places")
    BigDecimal price,
    
    @NotBlank(message = "Category is required")
    @Size(max = 100, message = "Category must not exceed 100 characters")
    String category,
    
    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    String imageUrl,
    
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    Integer rating,
    
    Map<String, String> specifications
) {
    
    // Constructor for creating new products (without ID)
    public ProductDTO(String name, String description, BigDecimal price, String category, 
                     String imageUrl, Integer rating, Map<String, String> specifications) {
        this(null, name, description, price, category, imageUrl, rating, specifications);
    }
}
