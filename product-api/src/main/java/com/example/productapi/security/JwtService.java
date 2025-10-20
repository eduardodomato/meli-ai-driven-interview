package com.example.productapi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT Service for token generation and validation
 * Only active when security profile is enabled
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "spring.profiles.active", havingValue = "security")
public class JwtService {
    
    @Value("${jwt.secret}")
    private String secretKey;
    
    @Value("${jwt.expiration}")
    private long jwtExpiration;
    
    /**
     * Generate JWT token for user with role
     */
    public String generateToken(String username, String role) {
        log.debug("Generating JWT token for user: {} with role: {}", username, role);
        Map<String, Object> claims = new HashMap<>();
        // Store role without ROLE_ prefix in JWT
        String roleWithoutPrefix = role.startsWith("ROLE_") ? role.substring(5) : role;
        claims.put("role", roleWithoutPrefix);
        return createToken(claims, username);
    }
    
    /**
     * Create JWT token with claims and subject
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
            .claims(claims)
            .subject(subject)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
            .signWith(getSignKey())
            .compact();
    }
    
    /**
     * Extract username from JWT token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    /**
     * Extract role from JWT token
     */
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }
    
    /**
     * Extract expiration date from JWT token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    /**
     * Extract specific claim from JWT token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * Extract all claims from JWT token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(getSignKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
    
    /**
     * Check if JWT token is expired
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    /**
     * Validate JWT token for specific user
     */
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
    
    /**
     * Get signing key for JWT
     */
    private SecretKey getSignKey() {
        byte[] keyBytes;
        try {
            // Prefer Base64 secrets when provided
            keyBytes = Decoders.BASE64.decode(secretKey);
        } catch (IllegalArgumentException ex) {
            // Fallback to raw UTF-8 bytes if not valid Base64
            log.warn("JWT secret is not Base64-encoded; falling back to UTF-8 bytes");
            keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        }

        // Ensure minimum 256-bit (32 bytes) length for HMAC-SHA
        if (keyBytes.length < 32) {
            log.warn("JWT secret is less than 256 bits; strengthening key material in-memory");
            byte[] strengthened = new byte[32];
            for (int i = 0; i < strengthened.length; i++) {
                strengthened[i] = keyBytes[i % keyBytes.length];
            }
            keyBytes = strengthened;
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }
}
