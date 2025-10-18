#!/bin/bash
echo "🐳 Building Product API Docker image..."

# Build the Docker image
docker build -t product-api:latest .

if [ $? -eq 0 ]; then
    echo "✅ Docker image built successfully!"
    echo "📦 Image: product-api:latest"
    echo "🚀 Run with: docker-compose up"
else
    echo "❌ Docker build failed!"
    exit 1
fi
