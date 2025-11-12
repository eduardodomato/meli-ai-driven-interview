@echo off
echo 🐳 Building Product API Docker image...

REM Build the Docker image
docker build -t product-api:latest .

if %errorlevel% equ 0 (
    echo ✅ Docker image built successfully!
    echo 📦 Image: product-api:latest
    echo 🚀 Run with: docker-compose up
) else (
    echo ❌ Docker build failed!
    exit /b 1
)
