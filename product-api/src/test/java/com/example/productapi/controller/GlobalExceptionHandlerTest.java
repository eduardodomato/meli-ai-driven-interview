package com.example.productapi.controller;

import com.example.productapi.exception.GlobalExceptionHandler;
import com.example.productapi.exception.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.lang.reflect.Method;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for GlobalExceptionHandler
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void testHandleIllegalArgumentException() {
        // Given
        IllegalArgumentException ex = new IllegalArgumentException("Test validation error");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgument(ex);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test validation error", response.getBody().getMessage());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals(400, response.getBody().getStatus());
    }

    @Test
    void testHandleNullPointerException() {
        // Given
        NullPointerException ex = new NullPointerException("Test null pointer");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleNullPointer(ex);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("An unexpected error occurred. Please try again later.", response.getBody().getMessage());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertEquals(500, response.getBody().getStatus());
    }

    @Test
    void testHandleMethodArgumentTypeMismatchException() {
        // Given
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
            "invalidValue", BigDecimal.class, "price", null, null);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleTypeMismatch(ex);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Invalid value 'invalidValue' for parameter 'price'"));
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals(400, response.getBody().getStatus());
    }

    @Test
    void testHandleMethodArgumentTypeMismatchExceptionWithNullRequiredType() {
        // Given
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
            "invalidValue", null, "price", null, null);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleTypeMismatch(ex);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Expected type: unknown"));
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals(400, response.getBody().getStatus());
    }
}
