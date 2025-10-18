package com.example.productapi.controller;

import com.example.productapi.dto.ProductDTO;
import com.example.productapi.mapper.ProductMapper;
import com.example.productapi.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for ProductController with DTO pattern
 */
@WebMvcTest(ProductController.class)
@TestPropertySource(properties = {
    "server.servlet.context-path=/api"
})
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;
    
    @MockBean
    private ProductMapper productMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductDTO sampleProductDTO;
    private List<ProductDTO> sampleProductsDTO;

    @BeforeEach
    void setUp() {
        // Create sample product DTO
        Map<String, String> specifications = new HashMap<>();
        specifications.put("RAM", "16GB");
        specifications.put("Storage", "512GB SSD");
        specifications.put("Processor", "Intel i7");

        sampleProductDTO = new ProductDTO(
            1L,
            "MacBook Pro 16-inch",
            "High-performance laptop for professionals",
            new BigDecimal("2499.99"),
            "Electronics",
            "https://example.com/macbook-pro.jpg",
            5,
            specifications
        );

        // Create sample products list
        ProductDTO product2 = new ProductDTO(
            2L,
            "iPhone 15 Pro",
            "Latest iPhone with advanced features",
            new BigDecimal("999.99"),
            "Electronics",
            "https://example.com/iphone-15.jpg",
            5,
            Map.of("Storage", "256GB", "Color", "Space Black")
        );

        sampleProductsDTO = Arrays.asList(sampleProductDTO, product2);
    }

    @Test
    void testGetAllProducts_ShouldReturnListOfProducts() throws Exception {
        // Given
        when(productService.getAllProducts()).thenReturn(sampleProductsDTO);

        // When & Then
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("MacBook Pro 16-inch"))
                .andExpect(jsonPath("$[0].price").value(2499.99))
                .andExpect(jsonPath("$[0].category").value("Electronics"))
                .andExpect(jsonPath("$[0].rating").value(5))
                .andExpect(jsonPath("$[0].specifications.RAM").value("16GB"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("iPhone 15 Pro"))
                .andExpect(jsonPath("$[1].price").value(999.99));
    }

    @Test
    void testGetProductById_ExistingProduct_ShouldReturnProduct() throws Exception {
        // Given
        when(productService.getProductById(1L)).thenReturn(Optional.of(sampleProductDTO));

        // When & Then
        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("MacBook Pro 16-inch"))
                .andExpect(jsonPath("$.price").value(2499.99))
                .andExpect(jsonPath("$.category").value("Electronics"))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.specifications.RAM").value("16GB"))
                .andExpect(jsonPath("$.specifications.Storage").value("512GB SSD"))
                .andExpect(jsonPath("$.specifications.Processor").value("Intel i7"));
    }

    @Test
    void testGetProductById_NonExistingProduct_ShouldReturn404() throws Exception {
        // Given
        when(productService.getProductById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/products/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateProduct_ValidProduct_ShouldReturnCreatedProduct() throws Exception {
        // Given
        ProductDTO newProductDTO = new ProductDTO(
            null,
            "New Laptop",
            "A new laptop",
            new BigDecimal("1299.99"),
            "Electronics",
            null, // imageUrl
            4,    // rating
            null  // specifications
        );

        ProductDTO createdProductDTO = new ProductDTO(
            3L,
            "New Laptop",
            "A new laptop",
            new BigDecimal("1299.99"),
            "Electronics",
            null, // imageUrl
            4,    // rating
            null  // specifications
        );

        when(productService.createProduct(any(ProductDTO.class))).thenReturn(createdProductDTO);

        // When & Then
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProductDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("New Laptop"))
                .andExpect(jsonPath("$.price").value(1299.99))
                .andExpect(jsonPath("$.category").value("Electronics"))
                .andExpect(jsonPath("$.rating").value(4));
    }

    @Test
    void testCreateProduct_InvalidProduct_ShouldReturn400() throws Exception {
        // Given - Invalid product (missing required fields)
        ProductDTO invalidProductDTO = new ProductDTO(
            null,
            "", // Empty name should fail validation
            null,
            new BigDecimal("-100"), // Negative price should fail validation
            null,
            null,
            null,
            null
        );

        // When & Then
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProductDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateProduct_ExistingProduct_ShouldReturnUpdatedProduct() throws Exception {
        // Given
        ProductDTO updatedProductDTO = new ProductDTO(
            null,
            "Updated MacBook Pro",
            "Updated description",
            new BigDecimal("2599.99"),
            "Electronics",
            null, // imageUrl
            5,    // rating
            null  // specifications
        );

        ProductDTO savedProductDTO = new ProductDTO(
            1L,
            "Updated MacBook Pro",
            "Updated description",
            new BigDecimal("2599.99"),
            "Electronics",
            null, // imageUrl
            5,    // rating
            null  // specifications
        );

        when(productService.updateProduct(eq(1L), any(ProductDTO.class))).thenReturn(Optional.of(savedProductDTO));

        // When & Then
        mockMvc.perform(put("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProductDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated MacBook Pro"))
                .andExpect(jsonPath("$.price").value(2599.99))
                .andExpect(jsonPath("$.description").value("Updated description"));
    }

    @Test
    void testUpdateProduct_NonExistingProduct_ShouldReturn404() throws Exception {
        // Given
        ProductDTO updatedProductDTO = new ProductDTO(
            null,
            "Updated Product",
            null,
            new BigDecimal("999.99"),
            "Electronics",
            null,
            null,
            null
        );

        when(productService.updateProduct(eq(999L), any(ProductDTO.class))).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(put("/products/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProductDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteProduct_ExistingProduct_ShouldReturn204() throws Exception {
        // Given
        when(productService.deleteProduct(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteProduct_NonExistingProduct_ShouldReturn404() throws Exception {
        // Given
        when(productService.deleteProduct(999L)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/products/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSearchProducts_WithValidParameters_ShouldReturnMatchingProducts() throws Exception {
        // Given
        when(productService.searchProducts("MacBook", "Electronics", 4, 5, new BigDecimal("2000"), new BigDecimal("3000")))
                .thenReturn(List.of(sampleProductDTO));

        // When & Then
        mockMvc.perform(get("/products/search")
                        .param("name", "MacBook")
                        .param("category", "Electronics")
                        .param("minRating", "4")
                        .param("maxRating", "5")
                        .param("minPrice", "2000")
                        .param("maxPrice", "3000"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("MacBook Pro 16-inch"))
                .andExpect(jsonPath("$[0].category").value("Electronics"));
    }

    @Test
    void testSearchProducts_WithNoParameters_ShouldReturnAllProducts() throws Exception {
        // Given
        when(productService.searchProducts(null, null, null, null, null, null))
                .thenReturn(sampleProductsDTO);

        // When & Then
        mockMvc.perform(get("/products/search"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testSearchProducts_WithPartialParameters_ShouldReturnMatchingProducts() throws Exception {
        // Given
        when(productService.searchProducts("MacBook", null, null, null, null, null))
                .thenReturn(List.of(sampleProductDTO));

        // When & Then
        mockMvc.perform(get("/products/search")
                        .param("name", "MacBook"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("MacBook Pro 16-inch"));
    }
}