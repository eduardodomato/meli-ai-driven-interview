package com.example.productapi.service;

import com.example.productapi.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for ProductService focusing on null safety
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceNullSafetyTest {

    @Mock
    private ResourceLoader resourceLoader;

    @Mock
    private Resource resource;

    private ProductService productService;

    @BeforeEach
    void setUp() throws IOException {
        // Mock the resource loading to return a JSON with products that have null names
        String jsonWithNullNames = """
            [
              {
                "id": 1,
                "name": "Valid Product",
                "description": "A valid product",
                "price": 100.00,
                "category": "Electronics",
                "rating": 5
              },
              {
                "id": 2,
                "name": null,
                "description": "Product with null name",
                "price": 50.00,
                "category": "Electronics",
                "rating": 3
              },
              {
                "id": 3,
                "name": "Another Valid Product",
                "description": "Another valid product",
                "price": 200.00,
                "category": "Home",
                "rating": 4
              }
            ]
            """;

        when(resourceLoader.getResource("classpath:products.json")).thenReturn(resource);
        when(resource.exists()).thenReturn(true);
        when(resource.getInputStream()).thenReturn(new ByteArrayInputStream(jsonWithNullNames.getBytes()));

        // Create the service with proper constructor parameters
        productService = new ProductService(resourceLoader, "classpath:products.json");
        
        // Initialize the service
        productService.init();
    }

    @Test
    void testSearchProductsWithNullNames_ShouldNotThrowNullPointerException() {
        // This test verifies that searching for products with null names doesn't throw NPE
        assertDoesNotThrow(() -> {
            List<Product> results = productService.searchProducts("Valid", null, null, null, null, null);
            assertEquals(2, results.size()); // Should find 2 products with "Valid" in name
        });
    }

    @Test
    void testSearchProductsWithNullCategory_ShouldNotThrowNullPointerException() {
        // This test verifies that searching with null category doesn't throw NPE
        assertDoesNotThrow(() -> {
            List<Product> results = productService.searchProducts(null, "Electronics", null, null, null, null);
            assertEquals(2, results.size()); // Should find 2 products in Electronics category
        });
    }

    @Test
    void testSearchProductsWithAllNullParameters_ShouldReturnAllProducts() {
        // This test verifies that searching with all null parameters returns all products
        assertDoesNotThrow(() -> {
            List<Product> results = productService.searchProducts(null, null, null, null, null, null);
            assertEquals(3, results.size()); // Should return all 3 products
        });
    }

    @Test
    void testSearchProductsWithNullNameFilter_ShouldHandleNullNamesGracefully() {
        // This test specifically checks that products with null names are handled gracefully
        List<Product> results = productService.searchProducts("NonExistent", null, null, null, null, null);
        assertEquals(0, results.size()); // Should return empty list, not throw NPE
    }
}
