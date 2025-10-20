package com.example.productapi.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI Configuration for Swagger UI
 * Defines security schemes and API documentation
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Product API",
        description = "RESTful API for managing Product entities with JWT authentication",
        version = "1.0.0",
        contact = @Contact(
            name = "Eduardo Domato",
            email = "eduardo@example.com"
        ),
        license = @License(
            name = "MIT License",
            url = "https://opensource.org/licenses/MIT"
        )
    ),
    servers = {
        @Server(
            url = "http://localhost:8080/api",
            description = "Development Server"
        )
    },
    security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "JWT Authorization header using the Bearer scheme. Example: \"Authorization: Bearer {token}\""
)
@ConditionalOnProperty(name = "spring.profiles.active", havingValue = "security")
public class OpenApiConfig {
    // Configuration class - no additional methods needed
}
