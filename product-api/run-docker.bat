@echo off
echo 🚀 Starting Product API with Docker Compose...

REM Start services
docker-compose up -d

if %errorlevel% equ 0 (
    echo ⏳ Waiting for services to be ready...
    timeout /t 10 /nobreak > nul
    
    echo ✅ Services are running!
    echo 🌐 API: http://localhost:8080/api
    echo 📊 Swagger UI: http://localhost:8080/api/swagger-ui.html
    echo ❤️ Health Check: http://localhost:8080/api/actuator/health
    echo 📈 Metrics: http://localhost:8080/api/actuator/metrics
    echo 🔧 Redis Commander: http://localhost:8081
) else (
    echo ❌ Failed to start services!
    exit /b 1
)
