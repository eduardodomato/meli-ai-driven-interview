package com.example.productapi.security.dto;

/**
 * Authentication response DTO
 */
public record AuthResponse(
    String token,
    String type
) {}
