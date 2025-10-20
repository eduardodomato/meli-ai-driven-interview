package com.example.productapi.security.dto;

import java.util.List;

/**
 * User information DTO
 */
public record UserInfo(
    String username,
    List<String> roles
) {}
