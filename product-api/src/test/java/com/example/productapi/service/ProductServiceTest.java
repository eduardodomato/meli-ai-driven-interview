package com.example.productapi.service;

import com.example.productapi.dto.ProductDTO;
import com.example.productapi.mapper.ProductMapper;
import com.example.productapi.metrics.ProductMetrics;
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
 * Test class for ProductService with DTO pattern
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private ProductMapper productMapper;
    
    @Mock
    private ProductMetrics productMetrics;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, productMapper, productMetrics);
    }

    @Test
    void testGetAllProducts() {
        // Given
        Product product1 = createSampleProduct(1L, "Laptop", "Electronics", new BigDecimal("999.99"));
        Product product2 = createSampleProduct(2L, "Phone", "Electronics", new BigDecimal("599.99"));
        List<Product> expectedProducts = List.of(product1, product2);
        
        ProductDTO productDTO1 = createSampleProductDTO(1L, "Laptop", "Electronics", new BigDecimal("999.99"));
        ProductDTO productDTO2 = createSampleProductDTO(2L, "Phone", "Electronics", new BigDecimal("599.99"));
        List<ProductDTO> expectedDTOs = List.of(productDTO1, productDTO2);
        
        when(productRepository.findAll()).thenReturn(expectedProducts);
        when(productMapper.toDTOList(expectedProducts)).thenReturn(expectedDTOs);

        // When
        List<ProductDTO> result = productService.getAllProducts();

        // Then
        assertEquals(2, result.size());
        assertEquals("Laptop", result.get(0).name());
        assertEquals("Phone", result.get(1).name());
        verify(productRepository).findAll();
        verify(productMapper).toDTOList(expectedProducts);
    }

    @Test
    void testGetProductById_ExistingProduct() {
        // Given
        Product expectedProduct = createSampleProduct(1L, "Laptop", "Electronics", new BigDecimal("999.99"));
        ProductDTO expectedDTO = createSampleProductDTO(1L, "Laptop", "Electronics", new BigDecimal("999.99"));
        
        when(productRepository.findById(1L)).thenReturn(Optional.of(expectedProduct));
        when(productMapper.toDTOOptional(Optional.of(expectedProduct))).thenReturn(Optional.of(expectedDTO));

        // When
        Optional<ProductDTO> result = productService.getProductById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Laptop", result.get().name());
        verify(productRepository).findById(1L);
        verify(productMapper).toDTOOptional(Optional.of(expectedProduct));
    }

    @Test
    void testGetProductById_NonExistingProduct() {
        // Given
        when(productRepository.findById(999L)).thenReturn(Optional.empty());
        when(productMapper.toDTOOptional(Optional.empty())).thenReturn(Optional.empty());

        // When
        Optional<ProductDTO> result = productService.getProductById(999L);

        // Then
        assertFalse(result.isPresent());
        verify(productRepository).findById(999L);
        verify(productMapper).toDTOOptional(Optional.empty());
    }

    @Test
    void testCreateProduct() {
        // Given
        ProductDTO inputDTO = createSampleProductDTO(null, "New Laptop", "Electronics", new BigDecimal("1299.99"));
        Product inputProduct = createSampleProduct(null, "New Laptop", "Electronics", new BigDecimal("1299.99"));
        Product savedProduct = createSampleProduct(1L, "New Laptop", "Electronics", new BigDecimal("1299.99"));
        ProductDTO savedDTO = createSampleProductDTO(1L, "New Laptop", "Electronics", new BigDecimal("1299.99"));
        
        when(productMapper.toEntity(inputDTO)).thenReturn(inputProduct);
        when(productRepository.save(inputProduct)).thenReturn(savedProduct);
        when(productMapper.toDTO(savedProduct)).thenReturn(savedDTO);

        // When
        ProductDTO result = productService.createProduct(inputDTO);

        // Then
        assertNotNull(result.id());
        assertEquals("New Laptop", result.name());
        verify(productMapper).toEntity(inputDTO);
        verify(productRepository).save(inputProduct);
        verify(productMapper).toDTO(savedProduct);
    }

    @Test
    void testUpdateProduct_ExistingProduct() {
        // Given
        ProductDTO updatedDTO = createSampleProductDTO(null, "Updated Laptop", "Electronics", new BigDecimal("1399.99"));
        Product existingProduct = createSampleProduct(1L, "Old Laptop", "Electronics", new BigDecimal("999.99"));
        Product savedProduct = createSampleProduct(1L, "Updated Laptop", "Electronics", new BigDecimal("1399.99"));
        ProductDTO savedDTO = createSampleProductDTO(1L, "Updated Laptop", "Electronics", new BigDecimal("1399.99"));
        
        when(productRepository.existsById(1L)).thenReturn(true);
        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.update(existingProduct)).thenReturn(savedProduct);
        when(productMapper.toDTO(savedProduct)).thenReturn(savedDTO);

        // When
        Optional<ProductDTO> result = productService.updateProduct(1L, updatedDTO);

        // Then
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().id());
        assertEquals("Updated Laptop", result.get().name());
        verify(productRepository).existsById(1L);
        verify(productRepository).findById(1L);
        verify(productMapper).updateEntity(existingProduct, updatedDTO);
        verify(productRepository).update(existingProduct);
        verify(productMapper).toDTO(savedProduct);
    }

    @Test
    void testUpdateProduct_NonExistingProduct() {
        // Given
        ProductDTO updatedDTO = createSampleProductDTO(null, "Updated Laptop", "Electronics", new BigDecimal("1399.99"));
        
        when(productRepository.existsById(999L)).thenReturn(false);

        // When
        Optional<ProductDTO> result = productService.updateProduct(999L, updatedDTO);

        // Then
        assertFalse(result.isPresent());
        verify(productRepository).existsById(999L);
        verify(productRepository, never()).findById(any());
        verify(productMapper, never()).updateEntity(any(), any());
        verify(productRepository, never()).update(any());
    }

    @Test
    void testUpdateProduct_RaceCondition_ProductDeletedConcurrently() {
        // Given - Simulate race condition: product exists during existsById() but deleted before update()
        ProductDTO updatedDTO = createSampleProductDTO(null, "Updated Laptop", "Electronics", new BigDecimal("1399.99"));
        Product existingProduct = createSampleProduct(1L, "Old Laptop", "Electronics", new BigDecimal("999.99"));
        
        when(productRepository.existsById(1L)).thenReturn(true);
        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.update(existingProduct)).thenThrow(new IllegalArgumentException("Product with id 1 not found"));

        // When
        Optional<ProductDTO> result = productService.updateProduct(1L, updatedDTO);

        // Then - Should return empty Optional instead of throwing exception
        assertFalse(result.isPresent());
        verify(productRepository).existsById(1L);
        verify(productRepository).findById(1L);
        verify(productMapper).updateEntity(existingProduct, updatedDTO);
        verify(productRepository).update(existingProduct);
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
        Product product1 = createSampleProduct(1L, "Laptop", "Electronics", new BigDecimal("999.99"));
        List<Product> expectedProducts = List.of(product1);
        
        ProductDTO productDTO1 = createSampleProductDTO(1L, "Laptop", "Electronics", new BigDecimal("999.99"));
        List<ProductDTO> expectedDTOs = List.of(productDTO1);
        
        when(productRepository.search(null, "Electronics", null, null, null, null)).thenReturn(expectedProducts);
        when(productMapper.toDTOList(expectedProducts)).thenReturn(expectedDTOs);

        // When
        List<ProductDTO> result = productService.searchProducts(null, "Electronics", null, null, null, null);

        // Then
        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).name());
        verify(productRepository).search(null, "Electronics", null, null, null, null);
        verify(productMapper).toDTOList(expectedProducts);
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
    
    private ProductDTO createSampleProductDTO(Long id, String name, String category, BigDecimal price) {
        return new ProductDTO(
            id,
            name,
            "Sample description",
            price,
            category,
            null, // imageUrl
            4,    // rating
            null  // specifications
        );
    }
}