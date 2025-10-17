package com.example.productapi.service;

import com.example.productapi.model.Product;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service class for managing Product entities using JSON file persistence
 */
@Slf4j
@Service
public class ProductService {

    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;
    private final String dataFilePath;
    
    private List<Product> products;
    private final AtomicLong idGenerator = new AtomicLong();

    public ProductService(ResourceLoader resourceLoader, 
                         @Value("${app.data.file}") String dataFilePath) {
        this.resourceLoader = resourceLoader;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.dataFilePath = dataFilePath;
    }

    @PostConstruct
    public void init() {
        loadProducts();
        initializeIdGenerator();
    }

    private void loadProducts() {
        try {
            Resource resource = resourceLoader.getResource(dataFilePath);
            if (resource.exists()) {
                products = objectMapper.readValue(resource.getInputStream(), 
                    new TypeReference<List<Product>>() {});
                log.info("Loaded {} products from {}", products.size(), dataFilePath);
            } else {
                products = new ArrayList<>();
                log.warn("Data file {} not found, starting with empty product list", dataFilePath);
            }
        } catch (IOException e) {
            log.error("Error loading products from {}", dataFilePath, e);
            products = new ArrayList<>();
        }
    }

    private void initializeIdGenerator() {
        long maxId = products.stream()
            .mapToLong(Product::getId)
            .max()
            .orElse(0L);
        idGenerator.set(maxId + 1);
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    public Optional<Product> getProductById(Long id) {
        return products.stream()
            .filter(product -> Objects.equals(product.getId(), id))
            .findFirst();
    }

    public Product createProduct(Product product) {
        product.setId(idGenerator.getAndIncrement());
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        
        if (product.getActive() == null) {
            product.setActive(true);
        }
        
        products.add(product);
        log.info("Created product with id: {}", product.getId());
        return product;
    }

    public Optional<Product> updateProduct(Long id, Product updatedProduct) {
        return products.stream()
            .filter(product -> Objects.equals(product.getId(), id))
            .findFirst()
            .map(existingProduct -> {
                existingProduct.setName(updatedProduct.getName());
                existingProduct.setDescription(updatedProduct.getDescription());
                existingProduct.setPrice(updatedProduct.getPrice());
                existingProduct.setCategory(updatedProduct.getCategory());
                existingProduct.setStock(updatedProduct.getStock());
                existingProduct.setActive(updatedProduct.getActive());
                existingProduct.setUpdatedAt(LocalDateTime.now());
                
                log.info("Updated product with id: {}", id);
                return existingProduct;
            });
    }

    public boolean deleteProduct(Long id) {
        boolean removed = products.removeIf(product -> Objects.equals(product.getId(), id));
        if (removed) {
            log.info("Deleted product with id: {}", id);
        }
        return removed;
    }

    public List<Product> getProductsByCategory(String category) {
        return products.stream()
            .filter(product -> category.equalsIgnoreCase(product.getCategory()))
            .toList();
    }

    public List<Product> getActiveProducts() {
        return products.stream()
            .filter(Product::getActive)
            .toList();
    }

    public List<Product> searchProductsByName(String name) {
        return products.stream()
            .filter(product -> product.getName().toLowerCase().contains(name.toLowerCase()))
            .toList();
    }
}
