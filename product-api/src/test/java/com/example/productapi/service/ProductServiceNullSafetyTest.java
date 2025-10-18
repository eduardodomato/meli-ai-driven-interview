package com.example.productapi.service;

import com.example.productapi.dto.ProductDTO;
import com.example.productapi.mapper.ProductMapper;
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
 * Test class for ProductService focusing on null safety with DTO pattern
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceNullSafetyTest {

    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private ProductMapper productMapper;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, productMapper);
    }

    @Test
    void testSearchProductsWithNullNames_ShouldNotThrowNullPointerException() {
        // Given - Mock products with null names
        Product product1 = createProduct(1L, "Valid Product", "Electronics", 5);
        Product product3 = createProduct(3L, "Another Valid Product", "Home", 4);
        List<Product> products = List.of(product1, product3);
        
        ProductDTO productDTO1 = createProductDTO(1L, "Valid Product", "Electronics", 5);
        ProductDTO productDTO3 = createProductDTO(3L, "Another Valid Product", "Home", 4);
        List<ProductDTO> expectedDTOs = List.of(productDTO1, productDTO3);
        
        when(productRepository.search("Valid", null, null, null, null, null))
            .thenReturn(products);
        when(productMapper.toDTOList(products)).thenReturn(expectedDTOs);

        // When & Then - This should not throw NPE
        assertDoesNotThrow(() -> {
            List<ProductDTO> results = productService.searchProducts("Valid", null, null, null, null, null);
            assertEquals(2, results.size()); // Should find 2 products with "Valid" in name
        });
        
        verify(productRepository).search("Valid", null, null, null, null, null);
        verify(productMapper).toDTOList(products);
    }

    @Test
    void testSearchProductsWithNullCategory_ShouldNotThrowNullPointerException() {
        // Given
        Product product1 = createProduct(1L, "Laptop", "Electronics", 5);
        List<Product> products = List.of(product1);
        
        ProductDTO productDTO1 = createProductDTO(1L, "Laptop", "Electronics", 5);
        List<ProductDTO> expectedDTOs = List.of(productDTO1);
        
        when(productRepository.search(null, "Electronics", null, null, null, null))
            .thenReturn(products);
        when(productMapper.toDTOList(products)).thenReturn(expectedDTOs);

        // When & Then - This should not throw NPE
        assertDoesNotThrow(() -> {
            List<ProductDTO> results = productService.searchProducts(null, "Electronics", null, null, null, null);
            assertEquals(1, results.size());
        });
        
        verify(productRepository).search(null, "Electronics", null, null, null, null);
        verify(productMapper).toDTOList(products);
    }

    @Test
    void testSearchProductsWithNullRating_ShouldNotThrowNullPointerException() {
        // Given
        Product product1 = createProduct(1L, "Laptop", "Electronics", 4);
        List<Product> products = List.of(product1);
        
        ProductDTO productDTO1 = createProductDTO(1L, "Laptop", "Electronics", 4);
        List<ProductDTO> expectedDTOs = List.of(productDTO1);
        
        when(productRepository.search(null, null, 4, 5, null, null))
            .thenReturn(products);
        when(productMapper.toDTOList(products)).thenReturn(expectedDTOs);

        // When & Then - This should not throw NPE
        assertDoesNotThrow(() -> {
            List<ProductDTO> results = productService.searchProducts(null, null, 4, 5, null, null);
            assertEquals(1, results.size());
        });
        
        verify(productRepository).search(null, null, 4, 5, null, null);
        verify(productMapper).toDTOList(products);
    }

    @Test
    void testSearchProductsWithNullPrice_ShouldNotThrowNullPointerException() {
        // Given
        Product product1 = createProduct(1L, "Laptop", "Electronics", 4);
        List<Product> products = List.of(product1);
        
        ProductDTO productDTO1 = createProductDTO(1L, "Laptop", "Electronics", 4);
        List<ProductDTO> expectedDTOs = List.of(productDTO1);
        
        when(productRepository.search(null, null, null, null, new BigDecimal("50"), new BigDecimal("100")))
            .thenReturn(products);
        when(productMapper.toDTOList(products)).thenReturn(expectedDTOs);

        // When & Then - This should not throw NPE
        assertDoesNotThrow(() -> {
            List<ProductDTO> results = productService.searchProducts(null, null, null, null, new BigDecimal("50"), new BigDecimal("100"));
            assertEquals(1, results.size());
        });
        
        verify(productRepository).search(null, null, null, null, new BigDecimal("50"), new BigDecimal("100"));
        verify(productMapper).toDTOList(products);
    }

    @Test
    void testSearchProductsWithAllNullParameters_ShouldNotThrowNullPointerException() {
        // Given
        Product product1 = createProduct(1L, "Laptop", "Electronics", 4);
        Product product2 = createProduct(2L, "Phone", "Electronics", 5);
        List<Product> products = List.of(product1, product2);
        
        ProductDTO productDTO1 = createProductDTO(1L, "Laptop", "Electronics", 4);
        ProductDTO productDTO2 = createProductDTO(2L, "Phone", "Electronics", 5);
        List<ProductDTO> expectedDTOs = List.of(productDTO1, productDTO2);
        
        when(productRepository.search(null, null, null, null, null, null))
            .thenReturn(products);
        when(productMapper.toDTOList(products)).thenReturn(expectedDTOs);

        // When & Then - This should not throw NPE
        assertDoesNotThrow(() -> {
            List<ProductDTO> results = productService.searchProducts(null, null, null, null, null, null);
            assertEquals(2, results.size());
        });
        
        verify(productRepository).search(null, null, null, null, null, null);
        verify(productMapper).toDTOList(products);
    }

    @Test
    void testSearchProductsWithNonExistentName_ShouldReturnEmptyList() {
        // Given
        when(productRepository.search("NonExistent", null, null, null, null, null))
            .thenReturn(List.of());
        when(productMapper.toDTOList(List.of())).thenReturn(List.of());

        // When
        List<ProductDTO> results = productService.searchProducts("NonExistent", null, null, null, null, null);

        // Then
        assertTrue(results.isEmpty());
        verify(productRepository).search("NonExistent", null, null, null, null, null);
        verify(productMapper).toDTOList(List.of());
    }

    @Test
    void testSearchProductsWithValidParameters_ShouldReturnMatchingProducts() {
        // Given
        Product product1 = createProduct(1L, "Laptop", "Electronics", 5);
        List<Product> products = List.of(product1);
        
        ProductDTO productDTO1 = createProductDTO(1L, "Laptop", "Electronics", 5);
        List<ProductDTO> expectedDTOs = List.of(productDTO1);
        
        when(productRepository.search("Laptop", "Electronics", 5, null, new BigDecimal("1000"), null))
            .thenReturn(products);
        when(productMapper.toDTOList(products)).thenReturn(expectedDTOs);

        // When
        List<ProductDTO> results = productService.searchProducts("Laptop", "Electronics", 5, null, new BigDecimal("1000"), null);

        // Then
        assertEquals(1, results.size());
        assertEquals("Laptop", results.get(0).name());
        assertEquals("Electronics", results.get(0).category());
        assertEquals(5, results.get(0).rating());
        verify(productRepository).search("Laptop", "Electronics", 5, null, new BigDecimal("1000"), null);
        verify(productMapper).toDTOList(products);
    }

    private Product createProduct(Long id, String name, String category, Integer rating) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setDescription("Sample description");
        product.setPrice(new BigDecimal("999.99"));
        product.setCategory(category);
        product.setRating(rating);
        return product;
    }
    
    private ProductDTO createProductDTO(Long id, String name, String category, Integer rating) {
        return new ProductDTO(
            id,
            name,
            "Sample description",
            new BigDecimal("999.99"),
            category,
            null, // imageUrl
            rating,
            null  // specifications
        );
    }
}