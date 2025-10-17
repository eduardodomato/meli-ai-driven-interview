package com.example.productapi.service;

import com.example.productapi.model.Product;
import com.example.productapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing Product entities
 * Contains business logic and delegates data access to ProductRepository
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getAllProducts() {
        log.info("Retrieving all products");
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        log.info("Retrieving product with id: {}", id);
        return productRepository.findById(id);
    }

    public Product createProduct(Product product) {
        log.info("Creating new product: {}", product.getName());
        return productRepository.save(product);
    }

    public Optional<Product> updateProduct(Long id, Product updatedProduct) {
        log.info("Updating product with id: {}", id);
        
        if (!productRepository.existsById(id)) {
            return Optional.empty();
        }
        
        updatedProduct.setId(id);
        Product savedProduct = productRepository.update(updatedProduct);
        return Optional.of(savedProduct);
    }

    public boolean deleteProduct(Long id) {
        log.info("Deleting product with id: {}", id);
        return productRepository.deleteById(id);
    }

    public List<Product> searchProducts(String name, String category, Integer minRating, Integer maxRating, BigDecimal minPrice, BigDecimal maxPrice) {
        log.info("Searching products with criteria - name: {}, category: {}, minRating: {}, maxRating: {}, minPrice: {}, maxPrice: {}", 
                name, category, minRating, maxRating, minPrice, maxPrice);
        
        return productRepository.search(name, category, minRating, maxRating, minPrice, maxPrice);
    }
}
