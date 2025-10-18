package com.example.productapi.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Simple metrics component for tracking business operations
 * Uses only counters - no timers or complex metrics
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProductMetrics {

    private final MeterRegistry meterRegistry;
    
    // Lazy initialization of counters
    private Counter productsCreatedCounter;
    private Counter productsUpdatedCounter;
    private Counter productsDeletedCounter;
    private Counter productsRetrievedCounter;
    private Counter searchOperationsCounter;

    /**
     * Record a product creation operation
     */
    public void recordProductCreated() {
        if (productsCreatedCounter == null) {
            productsCreatedCounter = Counter.builder("product.operations.created")
                .description("Total number of products created")
                .register(meterRegistry);
        }
        productsCreatedCounter.increment();
        log.debug("Recorded product creation metric");
    }

    /**
     * Record a product update operation
     */
    public void recordProductUpdated() {
        if (productsUpdatedCounter == null) {
            productsUpdatedCounter = Counter.builder("product.operations.updated")
                .description("Total number of products updated")
                .register(meterRegistry);
        }
        productsUpdatedCounter.increment();
        log.debug("Recorded product update metric");
    }

    /**
     * Record a product deletion operation
     */
    public void recordProductDeleted() {
        if (productsDeletedCounter == null) {
            productsDeletedCounter = Counter.builder("product.operations.deleted")
                .description("Total number of products deleted")
                .register(meterRegistry);
        }
        productsDeletedCounter.increment();
        log.debug("Recorded product deletion metric");
    }

    /**
     * Record a product retrieval operation
     */
    public void recordProductRetrieved() {
        if (productsRetrievedCounter == null) {
            productsRetrievedCounter = Counter.builder("product.operations.retrieved")
                .description("Total number of products retrieved")
                .register(meterRegistry);
        }
        productsRetrievedCounter.increment();
        log.debug("Recorded product retrieval metric");
    }

    /**
     * Record a search operation
     */
    public void recordSearchOperation(String category, Integer minRating, BigDecimal minPrice) {
        if (searchOperationsCounter == null) {
            searchOperationsCounter = Counter.builder("product.operations.search")
                .description("Total number of search operations performed")
                .register(meterRegistry);
        }
        searchOperationsCounter.increment();
        log.debug("Recorded search operation metric with criteria - category: {}, minRating: {}, minPrice: {}", 
                 category, minRating, minPrice);
    }
}
