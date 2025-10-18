# How to Run the Product API Application

This guide will walk you through running the Product API application step by step. The application is a Spring Boot REST API that manages product data using JSON file persistence.

## Prerequisites

Before running the application, make sure you have the following installed on your system:

### Required Software
- **Java 21** or higher
- **Maven 3.6+** (for building and running the application)
- **Git** (for cloning the repository)

### How to Check Your Installation

**Check Java version:**
```bash
java -version
```
You should see output like: `openjdk version "21.x.x"` or `java version "21.x.x"`

**Check Maven version:**
```bash
mvn -version
```
You should see output like: `Apache Maven 3.x.x`

## Getting Started

### 1. Clone the Repository (if not already done)
```bash
git clone <repository-url>
cd meli-ai-driven-interview/product-api
```

### 2. Build the Application
```bash
mvn clean compile
```

### 3. Run Tests (Optional but Recommended)
```bash
mvn test
```
This will run all unit tests to ensure everything is working correctly.

### 4. Start the Application

#### Option A: Using Maven (Recommended for Development)
```bash
mvn spring-boot:run
```

#### Option B: Build and Run JAR
```bash
# Build the JAR file
mvn clean package

# Run the JAR file
java -jar target/product-api-0.0.1-SNAPSHOT.jar
```

#### Option C: Using IDE
If you're using an IDE like IntelliJ IDEA or Eclipse:
1. Import the project as a Maven project
2. Navigate to `ProductApiApplication.java`
3. Right-click and select "Run ProductApiApplication"

## Application Details

### Server Configuration
- **Port:** 8080
- **Context Path:** `/api`
- **Base URL:** `http://localhost:8080/api`

### Available Endpoints

Once the application is running, you can access the following endpoints:

#### Product Management
- `GET /api/products` - Get all products
- `GET /api/products/{id}` - Get product by ID
- `POST /api/products` - Create a new product
- `PUT /api/products/{id}` - Update a product
- `DELETE /api/products/{id}` - Delete a product

#### Product Search
- `GET /api/products/search` - Search products with flexible criteria
  - Query parameters: `name`, `category`, `minRating`, `maxRating`, `minPrice`, `maxPrice`

#### API Documentation
- **Swagger UI:** `http://localhost:8080/api/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8080/api/api-docs`

#### Monitoring and Health Checks (Actuator)
- **Health Check:** `http://localhost:8080/api/actuator/health`
- **Application Info:** `http://localhost:8080/api/actuator/info`
- **Metrics:** `http://localhost:8080/api/actuator/metrics`
- **Environment:** `http://localhost:8080/api/actuator/env`

## Testing the Application

### 1. Using Swagger UI (Easiest Method)
1. Open your browser and go to: `http://localhost:8080/api/swagger-ui.html`
2. You'll see an interactive API documentation interface
3. Click on any endpoint to expand it
4. Click "Try it out" to test the endpoint
5. Fill in the required parameters and click "Execute"

### 2. Using curl Commands

**Get all products:**
```bash
curl -X GET "http://localhost:8080/api/products"
```

**Get a specific product:**
```bash
curl -X GET "http://localhost:8080/api/products/1"
```

**Create a new product:**
```bash
curl -X POST "http://localhost:8080/api/products" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Product",
    "description": "A test product",
    "price": 99.99,
    "category": "Electronics",
    "rating": 4
  }'
```

**Search products:**
```bash
# Search by name
curl -X GET "http://localhost:8080/api/products/search?name=laptop"

# Search by category
curl -X GET "http://localhost:8080/api/products/search?category=Electronics"

# Search by rating range
curl -X GET "http://localhost:8080/api/products/search?minRating=4&maxRating=5"

# Search by price range
curl -X GET "http://localhost:8080/api/products/search?minPrice=100&maxPrice=1000"
```

### 3. Testing Actuator Endpoints

**Health Check:**
```bash
curl -X GET "http://localhost:8080/api/actuator/health"
```

**Application Info:**
```bash
curl -X GET "http://localhost:8080/api/actuator/info"
```

**Available Metrics:**
```bash
curl -X GET "http://localhost:8080/api/actuator/metrics"
```

**Specific Metric (e.g., product operations):**
```bash
curl -X GET "http://localhost:8080/api/actuator/metrics/product.operations.created"
```

**Environment Configuration:**
```bash
curl -X GET "http://localhost:8080/api/actuator/env"
```

### 4. Using Postman
1. Import the OpenAPI specification from: `http://localhost:8080/api/api-docs`
2. Create requests for each endpoint
3. Test the API functionality

## Sample Data

The application comes with sample product data loaded from `src/main/resources/products.json`. This includes:
- Laptop Pro 15
- Wireless Headphones
- Coffee Maker
- Running Shoes
- Smartphone

## Troubleshooting

### Common Issues and Solutions

#### 1. Port Already in Use
**Error:** `Port 8080 was already in use`

**Solution:**
- Stop any other application running on port 8080
- Or change the port in `src/main/resources/application.yml`:
  ```yaml
  server:
    port: 8081  # Change to a different port
  ```

#### 2. Java Version Issues
**Error:** `UnsupportedClassVersionError` or similar Java version errors

**Solution:**
- Ensure you have Java 21 installed
- Check your `JAVA_HOME` environment variable
- Update your IDE's Java SDK settings

#### 3. Maven Build Failures
**Error:** Build fails with dependency issues

**Solution:**
```bash
# Clean and rebuild
mvn clean install

# If still failing, try updating dependencies
mvn clean install -U
```

#### 4. Application Won't Start
**Error:** Application fails to start

**Solution:**
- Check the console output for specific error messages
- Ensure all required dependencies are available
- Check if the `products.json` file exists in `src/main/resources/`

### Logs and Debugging

The application uses SLF4J with Logback for logging. Log levels can be configured in `application.yml`:

```yaml
logging:
  level:
    com.example.productapi: DEBUG  # Set to INFO for less verbose logging
    org.springframework.web: DEBUG
```

## Development Mode

### Hot Reloading
For development with automatic restarts when code changes:

```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.devtools.restart.enabled=true"
```

### Running Specific Profiles
```bash
# Run with development profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Run with production profile
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

## Production Deployment

### Building for Production
```bash
# Build optimized JAR
mvn clean package -Pprod

# Run with production settings
java -jar target/product-api-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Environment Variables
You can override configuration using environment variables:
```bash
export SERVER_PORT=8080
export APP_DATA_FILE=classpath:products.json
java -jar target/product-api-0.0.1-SNAPSHOT.jar
```

## API Response Examples

### Successful Product Creation
```json
{
  "id": 6,
  "name": "New Product",
  "description": "A new product",
  "price": 199.99,
  "category": "Electronics",
  "rating": 5,
  "createdAt": "2024-01-20T10:30:00",
  "updatedAt": "2024-01-20T10:30:00"
}
```

### Error Response
```json
{
  "timestamp": "2024-01-20T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Minimum rating must be between 1 and 5"
}
```

## Monitoring and Metrics

The application includes Spring Boot Actuator for monitoring and observability:

### Custom Business Metrics
The application tracks the following business operations:
- **product.operations.created** - Total products created
- **product.operations.updated** - Total products updated
- **product.operations.deleted** - Total products deleted
- **product.operations.retrieved** - Total products retrieved
- **product.operations.search** - Total search operations

### How to Monitor Metrics
1. **Make API calls** to increment the counters
2. **Check metrics** at `/api/actuator/metrics`
3. **View specific metrics** like `/api/actuator/metrics/product.operations.created`
4. **Monitor health** at `/api/actuator/health`

### Example Monitoring Workflow
```bash
# 1. Check initial metrics
curl -X GET "http://localhost:8080/api/actuator/metrics/product.operations.retrieved"

# 2. Make API calls to increment counters
curl -X GET "http://localhost:8080/api/products"
curl -X GET "http://localhost:8080/api/products/1"

# 3. Check updated metrics
curl -X GET "http://localhost:8080/api/actuator/metrics/product.operations.retrieved"
```

## Next Steps

Once you have the application running:

1. **Explore the API** using Swagger UI
2. **Test all endpoints** to understand the functionality
3. **Try different search combinations** to see the flexible search capabilities
4. **Create your own products** and test the CRUD operations
5. **Monitor metrics** to see business operation tracking in action
6. **Check the logs** to understand how the application works internally
7. **Test health endpoints** to verify application monitoring capabilities

## Support

If you encounter any issues not covered in this guide:
1. Check the application logs for error messages
2. Verify all prerequisites are installed correctly
3. Ensure no firewall is blocking port 8080
4. Try running with different Java versions if available

Happy coding! 🚀
