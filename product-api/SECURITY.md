# Product API - Security Implementation

This document explains how to use the Profile-Based Security Implementation for the Product API.

## 🔐 Security Features

- **JWT Authentication** with Spring Security
- **Role-based Authorization** (USER, ADMIN)
- **Profile-based Configuration** (with/without security)
- **In-Memory User Management** for development
- **Conditional Security Components** based on Spring profiles

## 🚀 Quick Start

### Option 1: Run Without Security (Default)
```bash
# Windows
run-no-security.bat

# Linux/Mac
./run-no-security.sh

# Or manually
java -jar target/product-api-0.0.1-SNAPSHOT.jar --spring.profiles.active=no-security
```

### Option 2: Run With Security
```bash
# Windows
run-with-security.bat

# Linux/Mac
./run-with-security.sh

# Or manually
java -jar target/product-api-0.0.1-SNAPSHOT.jar --spring.profiles.active=security
```

### Option 3: Docker (Both Modes)
```bash
# Start both versions
docker-compose up -d

# Without Security: http://localhost:8080/api
# With Security: http://localhost:8081/api
```

## 👤 Test Users (Security Mode Only)

| Username | Password | Role  | Access Level |
|----------|----------|-------|--------------|
| admin    | admin123 | ADMIN | Full access (CRUD + Admin endpoints) |
| user     | user123  | USER  | Read-only access (GET operations) |

## 🔑 Authentication Flow

### 1. Login
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer"
}
```

### 2. Use Token
```bash
curl -X GET http://localhost:8081/api/products \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

## 📊 API Endpoints

### Public Endpoints (No Authentication Required)
- `GET /api/status/security` - Security configuration status
- `GET /api/actuator/health` - Health check
- `POST /api/auth/login` - User login (security mode only)

### Protected Endpoints (Authentication Required)

#### USER Role (Read Access)
- `GET /api/products` - List all products
- `GET /api/products/{id}` - Get product by ID
- `GET /api/products/search` - Search products

#### ADMIN Role (Full Access)
- All USER endpoints +
- `POST /api/products` - Create product
- `PUT /api/products/{id}` - Update product
- `DELETE /api/products/{id}` - Delete product
- `GET /api/swagger-ui.html` - Swagger UI
- `GET /api/actuator/**` - All actuator endpoints

## 🔧 Configuration

### Environment Variables
```bash
# JWT Configuration
JWT_SECRET=your-secret-key-here
JWT_EXPIRATION=86400000  # 24 hours in milliseconds

# Spring Profile
SPRING_PROFILES_ACTIVE=security  # or no-security
```

### Application Profiles

#### `no-security` (Default)
- No authentication required
- All endpoints publicly accessible
- Swagger UI open to all

#### `security`
- JWT authentication required
- Role-based authorization
- Admin-only Swagger UI

## 🐳 Docker Configuration

### Development (Both Modes)
```bash
docker-compose up -d
```
- **Port 8080**: No Security
- **Port 8081**: With Security

### Production (Security Only)
```bash
docker-compose -f docker-compose.prod.yml up -d
```
- Enhanced security settings
- Non-root user execution
- Read-only filesystem
- Security headers

## 🧪 Testing Security

### Check Security Status
```bash
curl http://localhost:8080/api/status/security  # No security
curl http://localhost:8081/api/status/security   # With security
```

### Test Authentication
```bash
# Test login
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "user", "password": "user123"}'

# Test protected endpoint without token (should fail)
curl http://localhost:8081/api/products

# Test protected endpoint with token (should succeed)
curl -H "Authorization: Bearer YOUR_TOKEN" http://localhost:8081/api/products
```

### Test Authorization
```bash
# Login as USER
USER_TOKEN=$(curl -s -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "user", "password": "user123"}' | jq -r '.token')

# Try to create product (should fail - USER role)
curl -X POST http://localhost:8081/api/products \
  -H "Authorization: Bearer $USER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","price":10.00,"category":"Test"}'

# Login as ADMIN
ADMIN_TOKEN=$(curl -s -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}' | jq -r '.token')

# Create product (should succeed - ADMIN role)
curl -X POST http://localhost:8081/api/products \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","price":10.00,"category":"Test"}'
```

## 🔍 Monitoring & Debugging

### Security Logs
```bash
# View security-related logs
docker-compose logs -f | grep -i security

# View authentication logs
docker-compose logs -f | grep -i auth
```

### Health Checks
```bash
# Check application health
curl http://localhost:8080/api/actuator/health  # No security
curl http://localhost:8081/api/actuator/health   # With security
```

## 🛠️ Development Tips

1. **Start without security** for rapid development
2. **Switch to security mode** when testing authentication
3. **Use Docker** to test both modes simultaneously
4. **Check security status** endpoint to verify configuration
5. **Use test users** provided for authentication testing

## 🔒 Security Considerations

- **JWT Secret**: Change default secret in production
- **Token Expiration**: Adjust based on security requirements
- **User Management**: Replace in-memory users with database
- **HTTPS**: Enable in production environments
- **Rate Limiting**: Consider adding for production use

## 📝 Next Steps

1. **Database Integration**: Replace in-memory user storage
2. **OAuth2 Provider**: Integrate with external identity provider
3. **Rate Limiting**: Add request rate limiting
4. **Audit Logging**: Implement security event logging
5. **Password Policies**: Add password complexity requirements
