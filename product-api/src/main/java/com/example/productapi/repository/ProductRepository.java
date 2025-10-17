package com.example.productapi.repository;

import com.example.productapi.model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Product data access operations
 * Abstracts the data persistence layer from business logic
 */
public interface ProductRepository {
    
    /**
     * Retrieve all products
     * @return List of all products
     */
    List<Product> findAll();
    
    /**
     * Find a product by its ID
     * @param id Product ID
     * @return Optional containing the product if found
     */
    Optional<Product> findById(Long id);
    
    /**
     * Save a new product
     * @param product Product to save
     * @return Saved product with generated ID
     */
    Product save(Product product);
    
    /**
     * Update an existing product
     * @param product Product to update
     * @return Updated product
     */
    Product update(Product product);
    
    /**
     * Delete a product by ID
     * @param id Product ID to delete
     * @return true if product was deleted, false if not found
     */
    boolean deleteById(Long id);
    
    /**
     * Search products with flexible criteria
     * @param name Product name filter (case-insensitive partial match)
     * @param category Product category filter
     * @param minRating Minimum rating filter
     * @param maxRating Maximum rating filter
     * @param minPrice Minimum price filter
     * @param maxPrice Maximum price filter
     * @return List of products matching the criteria
     */
    List<Product> search(String name, String category, Integer minRating, Integer maxRating, 
                        BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * Get the next available ID for new products
     * @return Next available ID
     */
    Long getNextId();
    
    /**
     * Check if a product exists by ID
     * @param id Product ID
     * @return true if product exists, false otherwise
     */
    boolean existsById(Long id);
}
