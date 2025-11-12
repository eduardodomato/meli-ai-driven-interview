package com.example.productapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.profiles.active=no-security",
    "management.endpoints.web.exposure.include=health,info,metrics,env",
    "management.endpoint.health.show-details=when-authorized",
    "management.endpoint.info.enabled=true",
    "management.endpoint.metrics.enabled=true"
})
class ProductApiApplicationTests {

    @Test
    void contextLoads() {
        // This test ensures that the Spring context loads successfully
        // with Actuator and custom metrics properly configured
    }
}
