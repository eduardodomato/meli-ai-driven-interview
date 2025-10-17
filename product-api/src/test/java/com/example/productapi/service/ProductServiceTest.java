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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for ProductService with repository layer
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository);
    }

    @Test
    void testGetAllProducts() {
        // Given
        Product product1 = createSampleProduct(1L, "Laptop", "Electronics", new BigDecimal("999.99"));
        Product product2 = createSampleProduct(2L, "Phone", "Electronics", new BigDecimal("599.99"));
        List<Product> expectedProducts = List.of(product1, product2);
        
        when(productRepository.findAll()).thenReturn(expectedProducts);

        // When
        List<Product> result = productService.getAllProducts();

        // Then
        assertEquals(2, result.size());
        assertEquals("Laptop", result.get(0).getName());
        assertEquals("Phone", result.get(1).getName());
        verify(productRepository).findAll();
    }

    @Test
    void testGetProductById_ExistingProduct() {
        // Given
        Product expectedProduct = createSampleProduct(1L, "Laptop", "Electronics", new BigDecimal("999.99"));
        when(productRepository.findById(1L)).thenReturn(Optional.of(expectedProduct));

        // When
        Optional<Product> result = productService.getProductById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Laptop", result.get().getName());
        verify(productRepository).findById(1L);
    }

    @Test
    void testGetProductById_NonExistingProduct() {
        // Given
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Product> result = productService.getProductById(999L);

        // Then
        assertFalse(result.isPresent());
        verify(productRepository).findById(999L);
    }

    @Test
    void testCreateProduct() {
        // Given
        Product inputProduct = createSampleProduct(null, "New Laptop", "Electronics", new BigDecimal("1299.99"));
        Product savedProduct = createSampleProduct(1L, "New Laptop", "Electronics", new BigDecimal("1299.99"));
        
        when(productRepository.save(inputProduct)).thenReturn(savedProduct);

        // When
        Product result = productService.createProduct(inputProduct);

        // Then
        assertNotNull(result.getId());
        assertEquals("New Laptop", result.getName());
        verify(productRepository).save(inputProduct);
    }

    @Test
    void testUpdateProduct_ExistingProduct() {
        // Given
        Product updatedProduct = createSampleProduct(null, "Updated Laptop", "Electronics", new BigDecimal("1399.99"));
        Product savedProduct = createSampleProduct(1L, "Updated Laptop", "Electronics", new BigDecimal("1399.99"));
        
        when(productRepository.existsById(1L)).thenReturn(true);
        when(productRepository.update(any(Product.class))).thenReturn(savedProduct);

        // When
        Optional<Product> result = productService.updateProduct(1L, updatedProduct);

        // Then
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("Updated Laptop", result.get().getName());
        verify(productRepository).existsById(1L);
        verify(productRepository).update(argThat(p -> p.getId().equals(1L)));
    }

    @Test
    void testUpdateProduct_NonExistingProduct() {
        // Given
        Product updatedProduct = createSampleProduct(null, "Updated Laptop", "Electronics", new BigDecimal("1399.99"));
        
        when(productRepository.existsById(999L)).thenReturn(false);

        // When
        Optional<Product> result = productService.updateProduct(999L, updatedProduct);

        // Then
        assertFalse(result.isPresent());
        verify(productRepository).existsById(999L);
        verify(productRepository, never()).update(any(Product.class));
    }

    @Test
    void testDeleteProduct_ExistingProduct() {
        // Given
        when(productRepository.deleteById(1L)).thenReturn(true);

        // When
        boolean result = productService.deleteProduct(1L);

        // Then
        assertTrue(result);
        verify(productRepository).deleteById(1L);
    }

    @Test
    void testDeleteProduct_NonExistingProduct() {
        // Given
        when(productRepository.deleteById(999L)).thenReturn(false);

        // When
        boolean result = productService.deleteProduct(999L);

        // Then
        assertFalse(result);
        verify(productRepository).deleteById(999L);
    }

    @Test
    void testSearchProducts() {
        // Given
        Product product1 = createSampleProduct(1L, "Gaming Laptop", "Electronics", new BigDecimal("1999.99"));
        Product product2 = createSampleProduct(2L, "Office Laptop", "Electronics", new BigDecimal("999.99"));
        List<Product> expectedProducts = List.of(product1, product2);
        
        when(productRepository.search("Laptop", "Electronics", 4, 5, 
            new BigDecimal("500"), new BigDecimal("2000")))
            .thenReturn(expectedProducts);

        // When
        List<Product> result = productService.searchProducts("Laptop", "Electronics", 4, 5, 
            new BigDecimal("500"), new BigDecimal("2000"));

        // Then
        assertEquals(2, result.size());
        verify(productRepository).search("Laptop", "Electronics", 4, 5, 
            new BigDecimal("500"), new BigDecimal("2000"));
    }

    @Test
    void testSearchProducts_WithNullValues() {
        // Given
        Product product1 = createSampleProduct(1L, "Laptop", "Electronics", new BigDecimal("999.99"));
        List<Product> expectedProducts = List.of(product1);
        
        when(productRepository.search(null, "Electronics", null, null, null, null))
            .thenReturn(expectedProducts);

        // When
        List<Product> result = productService.searchProducts(null, "Electronics", null, null, null, null);

        // Then
        assertEquals(1, result.size());
        verify(productRepository).search(null, "Electronics", null, null, null, null);
    }

    private Product createSampleProduct(Long id, String name, String category, BigDecimal price) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setDescription("Sample description");
        product.setPrice(price);
        product.setCategory(category);
        product.setRating(4);
        return product;
    }
}
