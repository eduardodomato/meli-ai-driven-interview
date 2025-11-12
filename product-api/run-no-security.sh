#!/bin/bash
echo "🚀 Starting Product API WITHOUT Security..."

# Check if Java is available
if ! command -v java &> /dev/null; then
    echo "Java is not installed or not in PATH. Please install Java 21 and try again."
    exit 1
fi

# Build the application
echo "Building application..."
./mvnw clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ Build failed!"
    exit 1
fi

echo "Starting application without security..."
java -jar target/product-api-0.0.1-SNAPSHOT.jar --spring.profiles.active=no-security
