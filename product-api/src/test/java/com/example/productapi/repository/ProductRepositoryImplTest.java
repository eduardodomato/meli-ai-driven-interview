package com.example.productapi.repository;

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
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for ProductRepositoryImpl
 */
@ExtendWith(MockitoExtension.class)
class ProductRepositoryImplTest {

    @Mock
    private ResourceLoader resourceLoader;

    @Mock
    private Resource resource;

    private ProductRepositoryImpl productRepository;

    @BeforeEach
    void setUp() throws IOException {
        // Mock the resource loading to return sample JSON data
        String sampleJson = """
            [
              {
                "id": 1,
                "name": "Laptop Pro 15",
                "description": "High-performance laptop",
                "price": 1299.99,
                "category": "Electronics",
                "imageUrl": "https://example.com/laptop.jpg",
                "rating": 5,
                "specifications": {
                  "RAM": "16GB",
                  "Storage": "512GB SSD"
                },
                "createdAt": "2024-01-15T10:30:00",
                "updatedAt": "2024-01-15T10:30:00"
              },
              {
                "id": 2,
                "name": "Wireless Headphones",
                "description": "Noise-cancelling headphones",
                "price": 199.99,
                "category": "Electronics",
                "imageUrl": "https://example.com/headphones.jpg",
                "rating": 4,
                "specifications": {
                  "Battery Life": "30 hours",
                  "Connectivity": "Bluetooth 5.0"
                },
                "createdAt": "2024-01-16T14:20:00",
                "updatedAt": "2024-01-16T14:20:00"
              },
              {
                "id": 3,
                "name": "Coffee Maker",
                "description": "Programmable coffee maker",
                "price": 89.99,
                "category": "Home & Kitchen",
                "imageUrl": "https://example.com/coffee-maker.jpg",
                "rating": 4,
                "specifications": {
                  "Capacity": "12 cups",
                  "Timer": "24-hour programmable"
                },
                "createdAt": "2024-01-17T09:15:00",
                "updatedAt": "2024-01-17T09:15:00"
              }
            ]
            """;

        when(resourceLoader.getResource("classpath:products.json")).thenReturn(resource);
        when(resource.exists()).thenReturn(true);
        when(resource.getInputStream()).thenReturn(new ByteArrayInputStream(sampleJson.getBytes()));

        productRepository = new ProductRepositoryImpl(resourceLoader, "classpath:products.json");
        productRepository.init();
    }

    @Test
    void testFindAll() {
        List<Product> products = productRepository.findAll();
        
        assertEquals(3, products.size());
        assertEquals("Laptop Pro 15", products.get(0).getName());
        assertEquals("Wireless Headphones", products.get(1).getName());
        assertEquals("Coffee Maker", products.get(2).getName());
    }

    @Test
    void testFindById_ExistingProduct() {
        Optional<Product> product = productRepository.findById(1L);
        
        assertTrue(product.isPresent());
        assertEquals("Laptop Pro 15", product.get().getName());
        assertEquals(1299.99, product.get().getPrice().doubleValue());
        assertEquals("Electronics", product.get().getCategory());
    }

    @Test
    void testFindById_NonExistingProduct() {
        Optional<Product> product = productRepository.findById(999L);
        
        assertFalse(product.isPresent());
    }

    @Test
    void testSave() {
        Product newProduct = new Product();
        newProduct.setName("New Product");
        newProduct.setDescription("A new product");
        newProduct.setPrice(new BigDecimal("99.99"));
        newProduct.setCategory("Test");
        newProduct.setRating(3);
        
        Product savedProduct = productRepository.save(newProduct);
        
        assertNotNull(savedProduct.getId());
        assertEquals("New Product", savedProduct.getName());
        assertEquals(4L, savedProduct.getId()); // Should be next ID after existing products
        assertNotNull(savedProduct.getCreatedAt());
        assertNotNull(savedProduct.getUpdatedAt());
    }

    @Test
    void testUpdate_ExistingProduct() {
        Product updatedProduct = new Product();
        updatedProduct.setId(1L);
        updatedProduct.setName("Updated Laptop");
        updatedProduct.setDescription("Updated description");
        updatedProduct.setPrice(new BigDecimal("1399.99"));
        updatedProduct.setCategory("Electronics");
        updatedProduct.setRating(5);
        
        Product result = productRepository.update(updatedProduct);
        
        assertEquals(1L, result.getId());
        assertEquals("Updated Laptop", result.getName());
        assertEquals("Updated description", result.getDescription());
        assertEquals(1399.99, result.getPrice().doubleValue());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void testUpdate_NonExistingProduct() {
        Product updatedProduct = new Product();
        updatedProduct.setId(999L);
        updatedProduct.setName("Non-existing Product");
        
        assertThrows(IllegalArgumentException.class, () -> {
            productRepository.update(updatedProduct);
        });
    }

    @Test
    void testDeleteById_ExistingProduct() {
        boolean deleted = productRepository.deleteById(1L);
        
        assertTrue(deleted);
        
        // Verify product is no longer found
        Optional<Product> product = productRepository.findById(1L);
        assertFalse(product.isPresent());
    }

    @Test
    void testDeleteById_NonExistingProduct() {
        boolean deleted = productRepository.deleteById(999L);
        
        assertFalse(deleted);
    }

    @Test
    void testSearch_ByName() {
        List<Product> results = productRepository.search("Laptop", null, null, null, null, null);
        
        assertEquals(1, results.size());
        assertEquals("Laptop Pro 15", results.get(0).getName());
    }

    @Test
    void testSearch_ByCategory() {
        List<Product> results = productRepository.search(null, "Electronics", null, null, null, null);
        
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(p -> "Electronics".equals(p.getCategory())));
    }

    @Test
    void testSearch_ByRating() {
        List<Product> results = productRepository.search(null, null, 5, null, null, null);
        
        assertEquals(1, results.size());
        assertEquals(5, results.get(0).getRating());
    }

    @Test
    void testSearch_ByPriceRange() {
        List<Product> results = productRepository.search(null, null, null, null, 
            new BigDecimal("100"), new BigDecimal("200"));
        
        assertEquals(1, results.size());
        assertEquals("Wireless Headphones", results.get(0).getName());
    }

    @Test
    void testSearch_MultipleCriteria() {
        List<Product> results = productRepository.search(null, "Electronics", 4, 5, 
            new BigDecimal("100"), new BigDecimal("1500"));
        
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(p -> "Electronics".equals(p.getCategory())));
        assertTrue(results.stream().allMatch(p -> p.getRating() >= 4 && p.getRating() <= 5));
    }

    @Test
    void testSearch_NoResults() {
        List<Product> results = productRepository.search("NonExistent", null, null, null, null, null);
        
        assertTrue(results.isEmpty());
    }

    @Test
    void testGetNextId() {
        Long nextId = productRepository.getNextId();
        
        assertEquals(4L, nextId); // Should be next ID after existing products (1, 2, 3)
    }

    @Test
    void testExistsById_ExistingProduct() {
        boolean exists = productRepository.existsById(1L);
        
        assertTrue(exists);
    }

    @Test
    void testExistsById_NonExistingProduct() {
        boolean exists = productRepository.existsById(999L);
        
        assertFalse(exists);
    }

    @Test
    void testSearchWithNullValues() {
        // Test that null values in products don't cause issues
        List<Product> results = productRepository.search(null, null, null, null, null, null);
        
        assertEquals(3, results.size()); // Should return all products
    }
}
