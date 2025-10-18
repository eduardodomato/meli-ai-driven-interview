package com.example.productapi.service;

import com.example.productapi.dto.ProductDTO;
import com.example.productapi.mapper.ProductMapper;
import com.example.productapi.metrics.ProductMetrics;
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
 * Uses DTOs for external communication while maintaining entities internally
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductMetrics productMetrics;

    public List<ProductDTO> getAllProducts() {
        log.info("Retrieving all products");
        List<Product> products = productRepository.findAll();
        productMetrics.recordProductRetrieved();
        return productMapper.toDTOList(products);
    }

    public Optional<ProductDTO> getProductById(Long id) {
        log.info("Retrieving product with id: {}", id);
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            productMetrics.recordProductRetrieved();
        }
        return productMapper.toDTOOptional(product);
    }

    public ProductDTO createProduct(ProductDTO productDTO) {
        log.info("Creating new product: {}", productDTO.name());
        Product product = productMapper.toEntity(productDTO);
        Product savedProduct = productRepository.save(product);
        productMetrics.recordProductCreated();
        return productMapper.toDTO(savedProduct);
    }

    public Optional<ProductDTO> updateProduct(Long id, ProductDTO updatedProductDTO) {
        log.info("Updating product with id: {}", id);
        
        if (!productRepository.existsById(id)) {
            return Optional.empty();
        }
        
        // Get existing product to preserve createdAt
        Optional<Product> existingProductOpt = productRepository.findById(id);
        if (existingProductOpt.isEmpty()) {
            return Optional.empty();
        }
        
        Product existingProduct = existingProductOpt.get();
        productMapper.updateEntity(existingProduct, updatedProductDTO);
        
        try {
            Product savedProduct = productRepository.update(existingProduct);
            productMetrics.recordProductUpdated();
            return Optional.of(productMapper.toDTO(savedProduct));
        } catch (IllegalArgumentException e) {
            // Handle race condition: product was deleted between existsById() check and update() call
            log.warn("Product with id {} was deleted concurrently during update operation", id);
            return Optional.empty();
        }
    }

    public boolean deleteProduct(Long id) {
        log.info("Deleting product with id: {}", id);
        boolean deleted = productRepository.deleteById(id);
        if (deleted) {
            productMetrics.recordProductDeleted();
        }
        return deleted;
    }

    public List<ProductDTO> searchProducts(String name, String category, Integer minRating, Integer maxRating, BigDecimal minPrice, BigDecimal maxPrice) {
        log.info("Searching products with criteria - name: {}, category: {}, minRating: {}, maxRating: {}, minPrice: {}, maxPrice: {}", 
                name, category, minRating, maxRating, minPrice, maxPrice);
        
        List<Product> products = productRepository.search(name, category, minRating, maxRating, minPrice, maxPrice);
        productMetrics.recordSearchOperation(category, minRating, minPrice);
        return productMapper.toDTOList(products);
    }
}
