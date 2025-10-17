package com.example.productapi.controller;

import com.example.productapi.model.Product;
import com.example.productapi.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for ProductController - Happy Path Scenarios
 */
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private Product sampleProduct;
    private List<Product> sampleProducts;

    @BeforeEach
    void setUp() {
        // Create sample product
        Map<String, String> specifications = new HashMap<>();
        specifications.put("RAM", "16GB");
        specifications.put("Storage", "512GB SSD");
        specifications.put("Processor", "Intel i7");

        sampleProduct = new Product();
        sampleProduct.setId(1L);
        sampleProduct.setName("Laptop Pro 15");
        sampleProduct.setDescription("High-performance laptop with 16GB RAM and 512GB SSD");
        sampleProduct.setPrice(new BigDecimal("1299.99"));
        sampleProduct.setCategory("Electronics");
        sampleProduct.setImageUrl("https://example.com/images/laptop-pro-15.jpg");
        sampleProduct.setRating(5);
        sampleProduct.setSpecifications(specifications);
        sampleProduct.setCreatedAt(LocalDateTime.of(2024, 1, 15, 10, 30));
        sampleProduct.setUpdatedAt(LocalDateTime.of(2024, 1, 15, 10, 30));

        // Create sample product list
        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Wireless Headphones");
        product2.setDescription("Noise-cancelling wireless headphones");
        product2.setPrice(new BigDecimal("199.99"));
        product2.setCategory("Electronics");
        product2.setImageUrl("https://example.com/images/headphones.jpg");
        product2.setRating(4);
        product2.setSpecifications(Map.of("Battery Life", "30 hours"));
        product2.setCreatedAt(LocalDateTime.of(2024, 1, 16, 14, 20));
        product2.setUpdatedAt(LocalDateTime.of(2024, 1, 16, 14, 20));

        sampleProducts = Arrays.asList(sampleProduct, product2);
    }

    @Test
    void getAllProducts_ShouldReturnListOfProducts() throws Exception {
        // Given
        when(productService.getAllProducts()).thenReturn(sampleProducts);

        // When & Then
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Laptop Pro 15"))
                .andExpect(jsonPath("$[0].price").value(1299.99))
                .andExpect(jsonPath("$[0].rating").value(5))
                .andExpect(jsonPath("$[0].specifications.RAM").value("16GB"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Wireless Headphones"))
                .andExpect(jsonPath("$[1].rating").value(4));
    }

    @Test
    void getProductById_WhenProductExists_ShouldReturnProduct() throws Exception {
        // Given
        when(productService.getProductById(1L)).thenReturn(Optional.of(sampleProduct));

        // When & Then
        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop Pro 15"))
                .andExpect(jsonPath("$.description").value("High-performance laptop with 16GB RAM and 512GB SSD"))
                .andExpect(jsonPath("$.price").value(1299.99))
                .andExpect(jsonPath("$.category").value("Electronics"))
                .andExpect(jsonPath("$.imageUrl").value("https://example.com/images/laptop-pro-15.jpg"))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.specifications.RAM").value("16GB"))
                .andExpect(jsonPath("$.specifications.Storage").value("512GB SSD"))
                .andExpect(jsonPath("$.specifications.Processor").value("Intel i7"));
    }

    @Test
    void createProduct_WithValidProduct_ShouldReturnCreatedProduct() throws Exception {
        // Given
        Product newProduct = new Product();
        newProduct.setName("New Laptop");
        newProduct.setDescription("A new laptop");
        newProduct.setPrice(new BigDecimal("999.99"));
        newProduct.setCategory("Electronics");
        newProduct.setImageUrl("https://example.com/new-laptop.jpg");
        newProduct.setRating(4);
        newProduct.setSpecifications(Map.of("RAM", "8GB", "Storage", "256GB"));

        Product savedProduct = new Product();
        savedProduct.setId(3L);
        savedProduct.setName("New Laptop");
        savedProduct.setDescription("A new laptop");
        savedProduct.setPrice(new BigDecimal("999.99"));
        savedProduct.setCategory("Electronics");
        savedProduct.setImageUrl("https://example.com/new-laptop.jpg");
        savedProduct.setRating(4);
        savedProduct.setSpecifications(Map.of("RAM", "8GB", "Storage", "256GB"));
        savedProduct.setCreatedAt(LocalDateTime.now());
        savedProduct.setUpdatedAt(LocalDateTime.now());

        when(productService.createProduct(any(Product.class))).thenReturn(savedProduct);

        // When & Then
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("New Laptop"))
                .andExpect(jsonPath("$.price").value(999.99))
                .andExpect(jsonPath("$.rating").value(4))
                .andExpect(jsonPath("$.specifications.RAM").value("8GB"))
                .andExpect(jsonPath("$.specifications.Storage").value("256GB"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void updateProduct_WhenProductExists_ShouldReturnUpdatedProduct() throws Exception {
        // Given
        Product updatedProduct = new Product();
        updatedProduct.setName("Updated Laptop Pro 15");
        updatedProduct.setDescription("Updated description");
        updatedProduct.setPrice(new BigDecimal("1399.99"));
        updatedProduct.setCategory("Electronics");
        updatedProduct.setImageUrl("https://example.com/updated-laptop.jpg");
        updatedProduct.setRating(5);
        updatedProduct.setSpecifications(Map.of("RAM", "32GB", "Storage", "1TB SSD"));

        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setName("Updated Laptop Pro 15");
        existingProduct.setDescription("Updated description");
        existingProduct.setPrice(new BigDecimal("1399.99"));
        existingProduct.setCategory("Electronics");
        existingProduct.setImageUrl("https://example.com/updated-laptop.jpg");
        existingProduct.setRating(5);
        existingProduct.setSpecifications(Map.of("RAM", "32GB", "Storage", "1TB SSD"));
        existingProduct.setCreatedAt(LocalDateTime.of(2024, 1, 15, 10, 30));
        existingProduct.setUpdatedAt(LocalDateTime.now());

        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(Optional.of(existingProduct));

        // When & Then
        mockMvc.perform(put("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Laptop Pro 15"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.price").value(1399.99))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.specifications.RAM").value("32GB"))
                .andExpect(jsonPath("$.specifications.Storage").value("1TB SSD"));
    }

    @Test
    void deleteProduct_WhenProductExists_ShouldReturnNoContent() throws Exception {
        // Given
        when(productService.deleteProduct(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void searchProducts_ByCategory_ShouldReturnProducts() throws Exception {
        // Given
        when(productService.searchProducts(null, "Electronics", null, null, null, null)).thenReturn(sampleProducts);

        // When & Then
        mockMvc.perform(get("/products/search")
                        .param("category", "Electronics"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].category").value("Electronics"))
                .andExpect(jsonPath("$[1].category").value("Electronics"));
    }

    @Test
    void searchProducts_ByMinRating_ShouldReturnProducts() throws Exception {
        // Given
        when(productService.searchProducts(null, null, 5, null, null, null)).thenReturn(List.of(sampleProduct));

        // When & Then
        mockMvc.perform(get("/products/search")
                        .param("minRating", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].rating").value(5))
                .andExpect(jsonPath("$[0].name").value("Laptop Pro 15"));
    }

    @Test
    void searchProducts_ByName_ShouldReturnProducts() throws Exception {
        // Given
        when(productService.searchProducts("Laptop", null, null, null, null, null)).thenReturn(List.of(sampleProduct));

        // When & Then
        mockMvc.perform(get("/products/search")
                        .param("name", "Laptop"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Laptop Pro 15"));
    }

    @Test
    void searchProducts_ByNameWithCaseInsensitive_ShouldReturnProducts() throws Exception {
        // Given
        when(productService.searchProducts("laptop", null, null, null, null, null)).thenReturn(List.of(sampleProduct));

        // When & Then
        mockMvc.perform(get("/products/search")
                        .param("name", "laptop"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Laptop Pro 15"));
    }

    @Test
    void searchProducts_ByPriceRange_ShouldReturnProducts() throws Exception {
        // Given
        when(productService.searchProducts(null, null, null, null, new BigDecimal("1000"), new BigDecimal("1500"))).thenReturn(List.of(sampleProduct));

        // When & Then
        mockMvc.perform(get("/products/search")
                        .param("minPrice", "1000")
                        .param("maxPrice", "1500"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Laptop Pro 15"));
    }

    @Test
    void searchProducts_ByRatingRange_ShouldReturnProducts() throws Exception {
        // Given
        when(productService.searchProducts(null, null, 4, 5, null, null)).thenReturn(sampleProducts);

        // When & Then
        mockMvc.perform(get("/products/search")
                        .param("minRating", "4")
                        .param("maxRating", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void searchProducts_WithMultipleCriteria_ShouldReturnProducts() throws Exception {
        // Given
        when(productService.searchProducts("Laptop", "Electronics", 5, null, new BigDecimal("1000"), null)).thenReturn(List.of(sampleProduct));

        // When & Then
        mockMvc.perform(get("/products/search")
                        .param("name", "Laptop")
                        .param("category", "Electronics")
                        .param("minRating", "5")
                        .param("minPrice", "1000"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Laptop Pro 15"))
                .andExpect(jsonPath("$[0].category").value("Electronics"))
                .andExpect(jsonPath("$[0].rating").value(5));
    }

    @Test
    void searchProducts_WithoutParameters_ShouldReturnAllProducts() throws Exception {
        // Given
        when(productService.searchProducts(null, null, null, null, null, null)).thenReturn(sampleProducts);

        // When & Then
        mockMvc.perform(get("/products/search"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void createProduct_WithCompleteSpecifications_ShouldReturnCreatedProduct() throws Exception {
        // Given
        Map<String, String> detailedSpecs = new HashMap<>();
        detailedSpecs.put("RAM", "16GB");
        detailedSpecs.put("Storage", "512GB SSD");
        detailedSpecs.put("Processor", "Intel i7");
        detailedSpecs.put("Screen Size", "15.6 inches");
        detailedSpecs.put("Operating System", "Windows 11");

        Product newProduct = new Product();
        newProduct.setName("Gaming Laptop");
        newProduct.setDescription("High-performance gaming laptop");
        newProduct.setPrice(new BigDecimal("1999.99"));
        newProduct.setCategory("Electronics");
        newProduct.setImageUrl("https://example.com/gaming-laptop.jpg");
        newProduct.setRating(5);
        newProduct.setSpecifications(detailedSpecs);

        Product savedProduct = new Product();
        savedProduct.setId(4L);
        savedProduct.setName("Gaming Laptop");
        savedProduct.setDescription("High-performance gaming laptop");
        savedProduct.setPrice(new BigDecimal("1999.99"));
        savedProduct.setCategory("Electronics");
        savedProduct.setImageUrl("https://example.com/gaming-laptop.jpg");
        savedProduct.setRating(5);
        savedProduct.setSpecifications(detailedSpecs);
        savedProduct.setCreatedAt(LocalDateTime.now());
        savedProduct.setUpdatedAt(LocalDateTime.now());

        when(productService.createProduct(any(Product.class))).thenReturn(savedProduct);

        // When & Then
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.name").value("Gaming Laptop"))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.specifications.RAM").value("16GB"))
                .andExpect(jsonPath("$.specifications.Processor").value("Intel i7"))
                .andExpect(jsonPath("$.specifications['Screen Size']").value("15.6 inches"))
                .andExpect(jsonPath("$.specifications['Operating System']").value("Windows 11"));
    }
}
