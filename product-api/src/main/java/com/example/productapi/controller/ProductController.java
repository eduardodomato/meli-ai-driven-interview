package com.example.productapi.controller;

import com.example.productapi.model.Product;
import com.example.productapi.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Product operations
 */
@Slf4j
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Product API", description = "RESTful API for managing Product entities")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieve a list of all products")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of products"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<Product>> getAllProducts() {
        log.info("Getting all products");
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieve a specific product by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product found"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "400", description = "Invalid product ID")
    })
    public ResponseEntity<Product> getProductById(
            @Parameter(description = "Product ID") @PathVariable Long id) {
        log.info("Getting product with id: {}", id);
        Optional<Product> product = productService.getProductById(id);
        return product.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a new product", description = "Create a new product in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Product created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid product data")
    })
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        log.info("Creating new product: {}", product.getName());
        Product createdProduct = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product", description = "Update an existing product by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product updated successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "400", description = "Invalid product data")
    })
    public ResponseEntity<Product> updateProduct(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @RequestBody Product product) {
        log.info("Updating product with id: {}", id);
        Optional<Product> updatedProduct = productService.updateProduct(id, product);
        return updatedProduct.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product", description = "Delete a product by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "400", description = "Invalid product ID")
    })
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product ID") @PathVariable Long id) {
        log.info("Deleting product with id: {}", id);
        boolean deleted = productService.deleteProduct(id);
        return deleted ? ResponseEntity.noContent().build() 
                      : ResponseEntity.notFound().build();
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get products by category", description = "Retrieve all products in a specific category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Products found"),
        @ApiResponse(responseCode = "404", description = "No products found in category")
    })
    public ResponseEntity<List<Product>> getProductsByCategory(
            @Parameter(description = "Product category") @PathVariable String category) {
        log.info("Getting products by category: {}", category);
        List<Product> products = productService.getProductsByCategory(category);
        return products.isEmpty() ? ResponseEntity.notFound().build() 
                                 : ResponseEntity.ok(products);
    }

    @GetMapping("/rating/{minRating}")
    @Operation(summary = "Get products by minimum rating", description = "Retrieve all products with rating greater than or equal to the specified minimum rating")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Products found"),
        @ApiResponse(responseCode = "404", description = "No products found with the specified rating")
    })
    public ResponseEntity<List<Product>> getProductsByRating(
            @Parameter(description = "Minimum rating (1-5)") @PathVariable Integer minRating) {
        log.info("Getting products with minimum rating: {}", minRating);
        List<Product> products = productService.getProductsByRating(minRating);
        return products.isEmpty() ? ResponseEntity.notFound().build() 
                                 : ResponseEntity.ok(products);
    }

    @GetMapping("/search")
    @Operation(summary = "Search products by name", description = "Search for products by name containing the search term")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Products found"),
        @ApiResponse(responseCode = "404", description = "No products found")
    })
    public ResponseEntity<List<Product>> searchProductsByName(
            @Parameter(description = "Search term") @RequestParam String name) {
        log.info("Searching products by name: {}", name);
        List<Product> products = productService.searchProductsByName(name);
        return products.isEmpty() ? ResponseEntity.notFound().build() 
                                 : ResponseEntity.ok(products);
    }
}
