#!/bin/bash
echo "🔐 Starting Product API WITH Security..."

# Check if Java is available
if ! command -v java &> /dev/null; then
    echo "Java is not installed or not in PATH. Please install Java 21 and try again."
    exit 1
fi

# Set JWT environment variables
export JWT_SECRET="mySecretKeyForLocalDevelopment"
export JWT_EXPIRATION="86400000"

# Build the application
echo "Building application..."
./mvnw clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ Build failed!"
    exit 1
fi

echo "Starting application with security..."
echo "Test users:"
echo "  👤 Admin: admin / admin123"
echo "  👤 User: user / user123"
echo ""
java -jar target/product-api-0.0.1-SNAPSHOT.jar --spring.profiles.active=security
