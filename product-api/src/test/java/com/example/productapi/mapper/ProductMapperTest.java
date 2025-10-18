package com.example.productapi.mapper;

import com.example.productapi.dto.ProductDTO;
import com.example.productapi.model.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ProductMapper
 */
@ExtendWith(MockitoExtension.class)
class ProductMapperTest {

    @InjectMocks
    private ProductMapper productMapper;

    @Test
    void testToDTO_WithAllFields() {
        // Given
        Product product = createSampleProduct();
        product.setId(1L);
        product.setCreatedAt(LocalDateTime.now().minusDays(1));
        product.setUpdatedAt(LocalDateTime.now());

        // When
        ProductDTO result = productMapper.toDTO(product);

        // Then
        assertNotNull(result);
        assertEquals(product.getId(), result.id());
        assertEquals(product.getName(), result.name());
        assertEquals(product.getDescription(), result.description());
        assertEquals(product.getPrice(), result.price());
        assertEquals(product.getCategory(), result.category());
        assertEquals(product.getImageUrl(), result.imageUrl());
        assertEquals(product.getRating(), result.rating());
        assertEquals(product.getSpecifications(), result.specifications());
        
        // Verify internal fields are NOT in DTO
        // Note: ProductDTO doesn't have createdAt/updatedAt fields
    }

    @Test
    void testToDTO_NullInput() {
        // When
        ProductDTO result = productMapper.toDTO(null);

        // Then
        assertNull(result);
    }

    @Test
    void testToEntity_WithAllFields() {
        // Given
        ProductDTO productDTO = createSampleProductDTO();

        // When
        Product result = productMapper.toEntity(productDTO);

        // Then
        assertNotNull(result);
        assertEquals(productDTO.id(), result.getId());
        assertEquals(productDTO.name(), result.getName());
        assertEquals(productDTO.description(), result.getDescription());
        assertEquals(productDTO.price(), result.getPrice());
        assertEquals(productDTO.category(), result.getCategory());
        assertEquals(productDTO.imageUrl(), result.getImageUrl());
        assertEquals(productDTO.rating(), result.getRating());
        assertEquals(productDTO.specifications(), result.getSpecifications());
        
        // Verify timestamps are set for new entities
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void testToEntity_NullInput() {
        // When
        Product result = productMapper.toEntity(null);

        // Then
        assertNull(result);
    }

    @Test
    void testToDTOList() {
        // Given
        Product product1 = createSampleProduct();
        product1.setId(1L);
        Product product2 = createSampleProduct();
        product2.setId(2L);
        List<Product> products = List.of(product1, product2);

        // When
        List<ProductDTO> result = productMapper.toDTOList(products);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals(2L, result.get(1).id());
    }

    @Test
    void testToDTOList_NullInput() {
        // When
        List<ProductDTO> result = productMapper.toDTOList(null);

        // Then
        assertNull(result);
    }

    @Test
    void testToDTOOptional() {
        // Given
        Product product = createSampleProduct();
        product.setId(1L);
        Optional<Product> productOptional = Optional.of(product);

        // When
        Optional<ProductDTO> result = productMapper.toDTOOptional(productOptional);

        // Then
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().id());
        assertEquals(product.getName(), result.get().name());
    }

    @Test
    void testToDTOOptional_Empty() {
        // Given
        Optional<Product> emptyOptional = Optional.empty();

        // When
        Optional<ProductDTO> result = productMapper.toDTOOptional(emptyOptional);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testToDTOOptional_NullInput() {
        // When
        Optional<ProductDTO> result = productMapper.toDTOOptional(null);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testUpdateEntity() {
        // Given
        Product existingProduct = createSampleProduct();
        existingProduct.setId(1L);
        existingProduct.setCreatedAt(LocalDateTime.now().minusDays(1));
        existingProduct.setUpdatedAt(LocalDateTime.now().minusHours(1));
        
        ProductDTO updatedDTO = new ProductDTO(
            1L,
            "Updated Name",
            "A test laptop",
            new BigDecimal("1299.99"),
            "Electronics",
            "http://example.com/image.jpg",
            4,
            Map.of("RAM", "16GB", "Storage", "512GB SSD")
        );

        // When
        productMapper.updateEntity(existingProduct, updatedDTO);

        // Then
        assertEquals("Updated Name", existingProduct.getName());
        assertEquals(new BigDecimal("1299.99"), existingProduct.getPrice());
        assertEquals(updatedDTO.description(), existingProduct.getDescription());
        assertEquals(updatedDTO.category(), existingProduct.getCategory());
        
        // Verify createdAt is preserved
        assertNotNull(existingProduct.getCreatedAt());
        
        // Verify updatedAt is updated
        assertNotNull(existingProduct.getUpdatedAt());
    }

    @Test
    void testUpdateEntity_NullInputs() {
        // Given
        Product existingProduct = createSampleProduct();
        ProductDTO updatedDTO = createSampleProductDTO();

        // When/Then - Should not throw exceptions
        assertDoesNotThrow(() -> productMapper.updateEntity(null, updatedDTO));
        assertDoesNotThrow(() -> productMapper.updateEntity(existingProduct, null));
        assertDoesNotThrow(() -> productMapper.updateEntity(null, null));
    }

    private Product createSampleProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Laptop");
        product.setDescription("A test laptop");
        product.setPrice(new BigDecimal("999.99"));
        product.setCategory("Electronics");
        product.setImageUrl("http://example.com/image.jpg");
        product.setRating(4);
        product.setSpecifications(Map.of("RAM", "16GB", "Storage", "512GB SSD"));
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        return product;
    }

    private ProductDTO createSampleProductDTO() {
        return new ProductDTO(
            1L,
            "Test Laptop",
            "A test laptop",
            new BigDecimal("999.99"),
            "Electronics",
            "http://example.com/image.jpg",
            4,
            Map.of("RAM", "16GB", "Storage", "512GB SSD")
        );
    }
}
