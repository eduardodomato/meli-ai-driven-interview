package com.example.productapi.mapper;

import com.example.productapi.dto.ProductDTO;
import com.example.productapi.model.Product;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Mapper for converting between Product entity and ProductDTO
 * Handles the separation of internal fields (createdAt, updatedAt) from external API responses
 */
@Component
public class ProductMapper {

    /**
     * Convert Product entity to ProductDTO
     * Excludes internal fields like createdAt and updatedAt
     */
    public ProductDTO toDTO(Product product) {
        if (product == null) {
            return null;
        }
        
        return new ProductDTO(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getCategory(),
            product.getImageUrl(),
            product.getRating(),
            product.getSpecifications()
        );
    }

    /**
     * Convert ProductDTO to Product entity
     * Sets createdAt and updatedAt to current time for new products
     */
    public Product toEntity(ProductDTO productDTO) {
        if (productDTO == null) {
            return null;
        }
        
        Product product = new Product(
            productDTO.name(),
            productDTO.description(),
            productDTO.price(),
            productDTO.category(),
            productDTO.imageUrl(),
            productDTO.rating(),
            productDTO.specifications()
        );
        
        // Set ID if provided (for updates)
        if (productDTO.id() != null) {
            product.setId(productDTO.id());
        }
        
        return product;
    }

    /**
     * Convert list of Product entities to list of ProductDTOs
     */
    public List<ProductDTO> toDTOList(List<Product> products) {
        if (products == null) {
            return null;
        }
        
        return products.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * Convert Optional<Product> to Optional<ProductDTO>
     */
    public Optional<ProductDTO> toDTOOptional(Optional<Product> productOptional) {
        if (productOptional == null) {
            return Optional.empty();
        }
        
        return productOptional.map(this::toDTO);
    }

    /**
     * Update existing Product entity with data from ProductDTO
     * Preserves createdAt and updates updatedAt
     * Only updates fields that are not null in the DTO
     */
    public void updateEntity(Product existingProduct, ProductDTO productDTO) {
        if (existingProduct == null || productDTO == null) {
            return;
        }
        
        // Only update fields that are not null in the DTO
        if (productDTO.name() != null) {
            existingProduct.setName(productDTO.name());
        }
        if (productDTO.description() != null) {
            existingProduct.setDescription(productDTO.description());
        }
        if (productDTO.price() != null) {
            existingProduct.setPrice(productDTO.price());
        }
        if (productDTO.category() != null) {
            existingProduct.setCategory(productDTO.category());
        }
        if (productDTO.imageUrl() != null) {
            existingProduct.setImageUrl(productDTO.imageUrl());
        }
        if (productDTO.rating() != null) {
            existingProduct.setRating(productDTO.rating());
        }
        if (productDTO.specifications() != null) {
            existingProduct.setSpecifications(productDTO.specifications());
        }
        
        existingProduct.setUpdatedAt(java.time.LocalDateTime.now());
        
        // Note: createdAt is preserved, only updatedAt is updated
    }
}
