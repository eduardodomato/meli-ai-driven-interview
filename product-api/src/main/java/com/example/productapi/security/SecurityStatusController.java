package com.example.productapi.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Security Status Controller
 * Provides information about current security configuration
 */
@RestController
@RequestMapping("/status")
public class SecurityStatusController {
    
    @Value("${spring.profiles.active:no-security}")
    private String activeProfile;
    
    @GetMapping("/security")
    public ResponseEntity<Map<String, Object>> getSecurityStatus() {
        Map<String, Object> status = new HashMap<>();
        boolean securityEnabled = "security".equals(activeProfile);
        
        status.put("securityEnabled", securityEnabled);
        status.put("activeProfile", activeProfile);
        status.put("authEndpoint", securityEnabled ? "/api/auth/login" : "N/A");
        
        if (securityEnabled) {
            status.put("testUsers", Map.of(
                "admin", Map.of("username", "admin", "password", "admin123", "role", "ADMIN"),
                "user", Map.of("username", "user", "password", "user123", "role", "USER")
            ));
        }
        
        return ResponseEntity.ok(status);
    }
}
