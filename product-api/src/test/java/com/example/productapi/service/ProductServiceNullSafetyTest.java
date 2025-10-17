package com.example.productapi.service;

import com.example.productapi.model.Product;
import com.example.productapi.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for ProductService focusing on null safety with repository layer
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceNullSafetyTest {

    @Mock
    private ProductRepository productRepository;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository);
    }

    @Test
    void testSearchProductsWithNullNames_ShouldNotThrowNullPointerException() {
        // Given - Mock products with null names
        Product product1 = createProduct(1L, "Valid Product", "Electronics", 5);
        Product product3 = createProduct(3L, "Another Valid Product", "Home", 4);
        
        when(productRepository.search("Valid", null, null, null, null, null))
            .thenReturn(List.of(product1, product3)); // Should find 2 products with "Valid" in name

        // When & Then - This should not throw NPE
        assertDoesNotThrow(() -> {
            List<Product> results = productService.searchProducts("Valid", null, null, null, null, null);
            assertEquals(2, results.size()); // Should find 2 products with "Valid" in name
        });
        
        verify(productRepository).search("Valid", null, null, null, null, null);
    }

    @Test
    void testSearchProductsWithNullCategory_ShouldNotThrowNullPointerException() {
        // Given
        Product product1 = createProduct(1L, "Laptop", "Electronics", 5);
        Product product2 = createProduct(2L, "Headphones", "Electronics", 4);
        
        when(productRepository.search(null, "Electronics", null, null, null, null))
            .thenReturn(List.of(product1, product2)); // Should find 2 products in Electronics category

        // When & Then
        assertDoesNotThrow(() -> {
            List<Product> results = productService.searchProducts(null, "Electronics", null, null, null, null);
            assertEquals(2, results.size()); // Should find 2 products in Electronics category
        });
        
        verify(productRepository).search(null, "Electronics", null, null, null, null);
    }

    @Test
    void testSearchProductsWithAllNullParameters_ShouldReturnAllProducts() {
        // Given
        Product product1 = createProduct(1L, "Laptop", "Electronics", 5);
        Product product2 = createProduct(2L, null, "Electronics", 3); // Product with null name
        Product product3 = createProduct(3L, "Coffee Maker", null, 4); // Product with null category
        
        List<Product> mockProducts = List.of(product1, product2, product3);
        when(productRepository.search(null, null, null, null, null, null))
            .thenReturn(mockProducts); // Should return all 3 products

        // When & Then
        assertDoesNotThrow(() -> {
            List<Product> results = productService.searchProducts(null, null, null, null, null, null);
            assertEquals(3, results.size()); // Should return all 3 products
        });
        
        verify(productRepository).search(null, null, null, null, null, null);
    }

    @Test
    void testSearchProductsWithNullNameFilter_ShouldHandleNullNamesGracefully() {
        // Given
        when(productRepository.search("NonExistent", null, null, null, null, null))
            .thenReturn(List.of()); // Should return empty list

        // When & Then
        List<Product> results = productService.searchProducts("NonExistent", null, null, null, null, null);
        assertEquals(0, results.size()); // Should return empty list, not throw NPE
        
        verify(productRepository).search("NonExistent", null, null, null, null, null);
    }

    @Test
    void testSearchProductsWithNullRatingValues_ShouldHandleGracefully() {
        // Given
        Product product1 = createProduct(1L, "Laptop", "Electronics", 5);
        Product product3 = createProduct(3L, "Coffee Maker", "Home", 4);
        
        when(productRepository.search(null, null, 4, 5, null, null))
            .thenReturn(List.of(product1, product3)); // Should find products with rating >= 4

        // When & Then
        assertDoesNotThrow(() -> {
            List<Product> results = productService.searchProducts(null, null, 4, 5, null, null);
            assertEquals(2, results.size()); // Should find 2 products with rating >= 4
        });
        
        verify(productRepository).search(null, null, 4, 5, null, null);
    }

    @Test
    void testSearchProductsWithNullPriceValues_ShouldHandleGracefully() {
        // Given
        Product product1 = createProduct(1L, "Laptop", "Electronics", 5);
        product1.setPrice(new BigDecimal("999.99"));
        Product product2 = createProduct(2L, "Headphones", "Electronics", 4);
        product2.setPrice(null); // Product with null price
        Product product3 = createProduct(3L, "Coffee Maker", "Home", 4);
        product3.setPrice(new BigDecimal("89.99"));
        
        when(productRepository.search(null, null, null, null, new BigDecimal("50"), new BigDecimal("100")))
            .thenReturn(List.of(product3)); // Should find products with price in range

        // When & Then
        assertDoesNotThrow(() -> {
            List<Product> results = productService.searchProducts(null, null, null, null, 
                new BigDecimal("50"), new BigDecimal("100"));
            assertEquals(1, results.size()); // Should find 1 product with price in range
        });
        
        verify(productRepository).search(null, null, null, null, 
            new BigDecimal("50"), new BigDecimal("100"));
    }

    private Product createProduct(Long id, String name, String category, Integer rating) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setDescription("Sample description");
        product.setPrice(new BigDecimal("99.99"));
        product.setCategory(category);
        product.setRating(rating);
        return product;
    }
}
