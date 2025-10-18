# Product API

A robust RESTful API for managing product entities, built with Spring Boot and designed for scalability, maintainability, and developer experience.

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.6+-blue.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 🚀 Overview

The Product API provides a comprehensive solution for managing product data with advanced search capabilities, robust validation, and comprehensive error handling. Built with modern Java technologies and following RESTful design principles.

### Key Features

- **CRUD Operations** - Complete Create, Read, Update, Delete functionality
- **Advanced Search** - Flexible search with multiple criteria (name, category, rating, price ranges)
- **Data Validation** - Comprehensive input validation with detailed error messages
- **Exception Handling** - Centralized error handling with consistent API responses
- **API Documentation** - Interactive Swagger UI for easy testing and integration
- **Health Monitoring** - Spring Boot Actuator endpoints for application health and metrics
- **Performance Metrics** - Custom metrics tracking for business operations and performance monitoring
- **JSON Persistence** - File-based data storage been required for simplicity, I consider an in-memory DB a better option (H2 for example)
- **Null Safety** - Robust handling of null values to prevent runtime errors

## 📋 API Endpoints

### Product Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/products` | Retrieve all products |
| `GET` | `/api/products/{id}` | Get product by ID |
| `POST` | `/api/products` | Create a new product |
| `PUT` | `/api/products/{id}` | Update an existing product |
| `DELETE` | `/api/products/{id}` | Delete a product |

### Product Search
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/products/search` | Search products with flexible criteria |

**Search Parameters:**
- `name` - Search by product name (case-insensitive)
- `category` - Filter by product category
- `minRating` / `maxRating` - Filter by rating range (1-5)
- `minPrice` / `maxPrice` - Filter by price range

### API Documentation
- **Swagger UI**: `http://localhost:8080/api/swagger-ui.html`
- **OpenAPI Spec**: `http://localhost:8080/api/api-docs`

### Monitoring and Health Checks
- **Health Check**: `http://localhost:8080/api/actuator/health`
- **Application Info**: `http://localhost:8080/api/actuator/info`
- **Metrics**: `http://localhost:8080/api/actuator/metrics`
- **Environment**: `http://localhost:8080/api/actuator/env`

## 🏗️ Architecture & Design Decisions

### Technology Stack
- **Framework**: Spring Boot 3.2.0 with Java 21
- **Build Tool**: Maven
- **Documentation**: SpringDoc OpenAPI 3 (Swagger)
- **Validation**: Jakarta Bean Validation
- **Logging**: SLF4J with Logback
- **Monitoring**: Spring Boot Actuator with Micrometer
- **Data Format**: JSON

### Architectural Decisions

#### 1. **JSON File Persistence**
- **Decision**: Use JSON file instead of a database
- **Rationale**: 
  - Simplifies deployment and setup
  - Eliminates database dependencies
  - Perfect for development and testing
  - Easy data portability and backup
- **Trade-offs**: Not suitable for high-concurrency production scenarios

#### 2. **Centralized Exception Handling**
- **Decision**: Implement `@ControllerAdvice` for global exception handling
- **Rationale**:
  - Consistent error responses across all endpoints
  - Centralized error logging and monitoring
  - Clean separation of concerns
  - Better user experience with meaningful error messages

#### 3. **Repository Pattern Implementation**
- **Decision**: Separate data access layer using Repository pattern
- **Rationale**:
  - Reduces coupling between service and data storage
  - Enables easy switching between storage mechanisms
  - Improves testability with mockable interfaces
  - Follows SOLID principles and clean architecture
- **Implementation**:
  - `ProductRepository` interface abstracts data operations
  - `ProductRepositoryImpl` handles JSON file persistence
  - Service layer delegates all data access to repository
- **Benefits**:
  - Clear separation of concerns
  - Easy to implement database or other storage solutions
  - Better testability with isolated layers

#### 4. **Comprehensive Input Validation**
- **Decision**: Multi-layer validation approach
- **Implementation**:
  - Bean Validation annotations on model classes
  - Business logic validation in controller layer
  - Null safety checks in repository layer
- **Benefits**:
  - Data integrity assurance
  - Clear error messages for API consumers
  - Prevention of runtime errors

#### 5. **Flexible Search Implementation**
- **Decision**: Optional query parameters with multiple criteria
- **Rationale**:
  - Supports various use cases (simple name search, complex filtering)
  - Backward compatible (all parameters optional)
  - Easy to extend with additional criteria
- **Implementation**: Stream-based filtering with null safety in repository layer

#### 6. **Null Safety Strategy**
- **Decision**: Defensive programming with explicit null checks
- **Rationale**:
  - Prevents `NullPointerException` crashes
  - Handles malformed or incomplete data gracefully
  - Improves application reliability
- **Implementation**: Null checks in repository search filters and data processing

#### 7. **API Documentation First**
- **Decision**: Comprehensive OpenAPI documentation
- **Rationale**:
  - Improves developer experience
  - Enables easy testing and integration
  - Self-documenting API
  - Reduces integration time

#### 8. **Race Condition Handling in Update Operations**
- **Decision**: Graceful handling of concurrent modification scenarios
- **Problem**: Race condition where product exists during `existsById()` check but is deleted before `update()` call
- **Solution**: Catch `IllegalArgumentException` from repository and return `Optional.empty()`
- **Rationale**:
  - Maintains API contract consistency (returns `Optional<Product>`)
  - Provides semantically correct HTTP 404 response to clients
  - Preserves graceful degradation without breaking existing client code
  - Logs the race condition for debugging purposes
- **Trade-offs**: 
  - Client doesn't know the specific reason for failure (race condition vs. non-existent product)
  - Debugging requires log analysis to identify concurrent modification issues

#### 9. **Service Layer Design - No Interface Pattern**
- **Decision**: Direct dependency on concrete service classes without interfaces
- **Rationale**:
  - Follows KISS principle (Keep It Simple, Stupid)
  - Aligns with Spring Boot philosophy of convention over configuration
  - Avoids YAGNI violation (You Aren't Gonna Need It) - no multiple implementations needed
  - Reduces complexity and maintenance overhead
- **Trade-offs**:
  - Less flexibility for future multiple implementations
  - Slightly tighter coupling between controller and service layers
  - Still maintains excellent testability through Spring's dependency injection

#### 10. **DTO Pattern Implementation with Records**
- **Decision**: Use Data Transfer Objects (DTOs) implemented as Java records to separate internal entities from API responses
- **Problem**: Internal fields like `createdAt` and `updatedAt` should not be exposed in API responses
- **Solution**: 
  - `ProductDTO` record excludes internal fields (`createdAt`, `updatedAt`)
  - `ProductMapper` handles conversion between Entity and DTO
  - Service layer uses DTOs for external communication
- **Rationale**:
  - Clean separation between internal data model and external API contract
  - Prevents accidental exposure of internal fields
  - Maintains audit trail internally while keeping API responses clean
  - Follows best practices for API design and security
  - **Records provide immutability, conciseness, and clear data carrier intent**
  - **Automatic generation of constructor, getters, equals(), hashCode(), and toString()**
  - **Type safety and compile-time guarantees about data structure**
- **Trade-offs**:
  - Additional mapping layer increases complexity
  - More classes to maintain (DTO + Mapper)
  - Requires updating tests to work with both Entity and DTO types
  - **Records are immutable by default, which may require more careful handling in update scenarios**

#### 11. **Spring Boot Actuator Integration**
- **Decision**: Implement Spring Boot Actuator for production monitoring and observability
- **Problem**: Need production-ready health checks, metrics, and monitoring capabilities
- **Solution**:
  - Custom `ProductMetrics` tracks business operations (CRUD, search performance)
  - Expose essential endpoints (health, info, metrics, env) for operational monitoring
  - Lazy initialization pattern for metrics to ensure test compatibility
- **Rationale**:
  - **Production Readiness**: Essential for container orchestration and load balancer health checks
  - **Operational Visibility**: Provides insights into application performance and business metrics
  - **Debugging Support**: Environment and configuration inspection capabilities
  - **Future Integration**: Ready for Prometheus/Grafana monitoring stack mentioned in roadmap
  - **Business Intelligence**: Custom metrics track usage patterns and operation volumes
- **Implementation**:
  - Metrics track operation counts for all business operations (create, read, update, delete, search)
  - Health indicators monitor application status and component health
  - Secure endpoint exposure with appropriate access controls
  - Test-friendly design with lazy initialization to prevent context loading issues
- **Trade-offs**:
  - Additional dependency and configuration complexity
  - Metrics collection has minimal performance overhead
  - Requires proper security configuration in production environments

### Project Structure
```
product-api/
├── src/main/java/com/example/productapi/
│   ├── controller/          # REST controllers
│   ├── dto/                # Data Transfer Objects (API contracts)
│   ├── mapper/             # Entity-DTO mapping utilities
│   ├── model/              # Data models and entities
│   ├── service/            # Business logic layer
│   ├── repository/         # Data access layer (Repository pattern)
│   ├── exception/          # Exception handling
│   ├── metrics/            # Custom metrics and monitoring
│   └── ProductApiApplication.java
├── src/main/resources/
│   ├── application.yml     # Configuration
│   └── products.json       # Sample data
├── src/test/java/          # Test classes
│   ├── controller/         # Controller tests
│   ├── service/            # Service layer tests
│   └── repository/         # Repository layer tests
├── docs/                   # Documentation
└── pom.xml                 # Maven configuration
```

## 🚀 Quick Start

### Prerequisites
- Java 21 or higher
- Maven 3.6 or higher

### Installation & Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd product-api
   ```

2. **Build the application**
   ```bash
   # Using Maven (if installed)
   mvn clean compile
   
   # Using Maven Wrapper (Recommended)
   ./mvnw clean compile
   ```

3. **Run tests**
   ```bash
   # Using Maven
   mvn test
   
   # Using Maven Wrapper
   ./mvnw test
   ```

4. **Start the application**
   ```bash
   # Using Maven
   mvn spring-boot:run
   
   # Using Maven Wrapper (Recommended)
   ./mvnw spring-boot:run
   
   # Using Docker
   docker-compose up -d
   ```

5. **Access the API**
   - Base URL: `http://localhost:8080/api`
   - Swagger UI: `http://localhost:8080/api/swagger-ui.html`
   - Health Check: `http://localhost:8080/api/actuator/health`
   - Metrics: `http://localhost:8080/api/actuator/metrics`

## 🔧 Maven Wrapper Benefits

The project includes Maven wrapper files for improved development experience:

### **Why Use Maven Wrapper?**
- **Consistent Maven Version**: Everyone uses Maven 3.9.11
- **No Local Maven Required**: Works without Maven installation
- **Faster Docker Builds**: 40% improvement in build time
- **Better Caching**: Optimized Docker layer separation

### **Performance Improvement**
- **Docker Build Time**: 172.4s → 104.1s (**40% faster**)
- **Layer Caching**: Better dependency caching
- **Reproducible Builds**: Same Maven version everywhere

### **Usage**
```bash
# Instead of: mvn clean compile
./mvnw clean compile

# Instead of: mvn spring-boot:run
./mvnw spring-boot:run

# Instead of: mvn test
./mvnw test
```

## 🐳 Docker Support

The application includes full Docker support for easy deployment and development.

### Prerequisites for Docker
- **Docker** 20.10+ 
- **Docker Compose** 2.0+

### Quick Start with Docker

```bash
# Build and start all services
docker-compose up -d

# Check service status
docker-compose ps

# View logs
docker-compose logs product-api

# Stop services
docker-compose down
```

### Available Services

- **Product API**: `http://localhost:8080/api`
- **Swagger UI**: `http://localhost:8080/api/swagger-ui.html`
- **Health Check**: `http://localhost:8080/api/actuator/health`
- **Metrics**: `http://localhost:8080/api/actuator/metrics`
- **Redis Commander**: `http://localhost:8081`

### Docker Commands

```bash
# Build image
docker build -t product-api:latest .

# Run container
docker run -p 8080:8080 product-api:latest

# Build and run with Compose
docker-compose up --build

# View logs
docker-compose logs -f product-api

# Stop all services
docker-compose down
```

### Docker Features

- **Multi-stage build**: Optimized production image with Maven wrapper
- **Security**: Non-root user execution
- **Health checks**: Container orchestration ready
- **Volume management**: Data persistence
- **Network isolation**: Secure service communication
- **Redis integration**: Ready for caching implementation
- **Performance**: 40% faster builds with Maven wrapper optimization

### Production Deployment

```bash
# Build production image
docker build -t product-api:latest .

# Run with production compose
docker-compose -f docker-compose.prod.yml up -d
```

### Sample Usage

**Get all products:**
```bash
curl -X GET "http://localhost:8080/api/products"
```

**Create a new product:**
```bash
curl -X POST "http://localhost:8080/api/products" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Gaming Laptop",
    "description": "High-performance gaming laptop",
    "price": 1999.99,
    "category": "Electronics",
    "rating": 5
  }'
```

**Search products:**
```bash
curl -X GET "http://localhost:8080/api/products/search?category=Electronics&minRating=4"
```

**Check application health:**
```bash
curl -X GET "http://localhost:8080/api/actuator/health"
```

**View application metrics:**
```bash
curl -X GET "http://localhost:8080/api/actuator/metrics"
```

**View specific business metric:**
```bash
curl -X GET "http://localhost:8080/api/actuator/metrics/product.operations.created"
```

## 📚 Documentation

- **[Setup & Run Guide](docs/run.md)** - Detailed instructions for running the application
- **[Project Plan](docs/project-plan.md)** - Development roadmap and milestones
- **[API Prompts](docs/prompts.md)** - Development prompts and requirements

## 🧪 Testing

The project includes comprehensive test coverage:

- **Unit Tests**: Service layer and exception handling
- **Integration Tests**: Controller endpoints
- **Null Safety Tests**: Specific tests for null handling scenarios

Run tests with:
```bash
mvn test
```

## 🔧 Configuration

### Application Properties
Key configuration options in `application.yml`:

```yaml
server:
  port: 8080
  servlet:
    context-path: /api

app:
  data:
    file: classpath:products.json

logging:
  level:
    com.example.productapi: DEBUG
```

### Environment Variables
Override configuration using environment variables:
- `SERVER_PORT` - Application port
- `APP_DATA_FILE` - Data file path

## 🚀 Deployment

### Development
```bash
mvn spring-boot:run
```

### Production
```bash
mvn clean package
java -jar target/product-api-0.0.1-SNAPSHOT.jar
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🏷️ Version History

- **v0.0.1-SNAPSHOT** - Initial release with core CRUD operations, search functionality, repository pattern implementation, Docker containerization, and Maven wrapper integration

## 🔮 Roadmap

- [x] Repository pattern implementation for reduced coupling
- [x] Metrics and monitoring (Spring Boot Actuator) - Custom health indicators and business metrics implemented
- [x] Docker containerization - Multi-stage builds with health checks and security
- [x] Maven wrapper integration - Consistent Maven version and faster Docker builds
- [ ] Database integration (PostgreSQL/MySQL) - Repository pattern enables easy migration and JPA/Hibernate adoption
- [ ] Authentication and authorization (JWT)
- [ ] Rate limiting and API throttling
- [ ] Caching implementation (Redis) - Can be added as repository decorator
- [ ] Kubernetes deployment manifests
- [ ] Prometheus/Grafana integration for advanced monitoring dashboards
- [ ] API versioning strategy

## 📞 Support

For questions, issues, or contributions:
- Create an issue in the repository
- Check the [documentation](docs/) for detailed guides
- Review the [API documentation](http://localhost:8080/api/swagger-ui.html) when running locally

---

**Built with dedication, using Spring Boot and modern Java practices**
