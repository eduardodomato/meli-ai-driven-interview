package com.example.productapi.repository;

import com.example.productapi.model.Product;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * JSON file-based implementation of ProductRepository
 * Handles all data access operations using Jackson for JSON processing
 */
@Slf4j
@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;
    private final String dataFilePath;
    
    private List<Product> products;
    private final AtomicLong idGenerator = new AtomicLong();

    public ProductRepositoryImpl(ResourceLoader resourceLoader, 
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

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return products.stream()
            .filter(product -> Objects.equals(product.getId(), id))
            .findFirst();
    }

    @Override
    public Product save(Product product) {
        product.setId(idGenerator.getAndIncrement());
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        
        products.add(product);
        log.info("Saved product with id: {}", product.getId());
        return product;
    }

    @Override
    public Product update(Product product) {
        return products.stream()
            .filter(p -> Objects.equals(p.getId(), product.getId()))
            .findFirst()
            .map(existingProduct -> {
                existingProduct.setName(product.getName());
                existingProduct.setDescription(product.getDescription());
                existingProduct.setPrice(product.getPrice());
                existingProduct.setCategory(product.getCategory());
                existingProduct.setImageUrl(product.getImageUrl());
                existingProduct.setRating(product.getRating());
                existingProduct.setSpecifications(product.getSpecifications());
                existingProduct.setUpdatedAt(LocalDateTime.now());
                
                log.info("Updated product with id: {}", product.getId());
                return existingProduct;
            })
            .orElseThrow(() -> new IllegalArgumentException("Product with id " + product.getId() + " not found"));
    }

    @Override
    public boolean deleteById(Long id) {
        boolean removed = products.removeIf(product -> Objects.equals(product.getId(), id));
        if (removed) {
            log.info("Deleted product with id: {}", id);
        }
        return removed;
    }

    @Override
    public List<Product> search(String name, String category, Integer minRating, Integer maxRating, 
                               BigDecimal minPrice, BigDecimal maxPrice) {
        return products.stream()
            .filter(product -> name == null || (product.getName() != null && product.getName().toLowerCase().contains(name.toLowerCase())))
            .filter(product -> category == null || (product.getCategory() != null && category.equalsIgnoreCase(product.getCategory())))
            .filter(product -> minRating == null || (product.getRating() != null && product.getRating() >= minRating))
            .filter(product -> maxRating == null || (product.getRating() != null && product.getRating() <= maxRating))
            .filter(product -> minPrice == null || (product.getPrice() != null && product.getPrice().compareTo(minPrice) >= 0))
            .filter(product -> maxPrice == null || (product.getPrice() != null && product.getPrice().compareTo(maxPrice) <= 0))
            .toList();
    }

    @Override
    public Long getNextId() {
        return idGenerator.get();
    }

    @Override
    public boolean existsById(Long id) {
        return products.stream()
            .anyMatch(product -> Objects.equals(product.getId(), id));
    }
}
