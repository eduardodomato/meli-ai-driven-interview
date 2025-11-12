@echo off
echo 🚀 Starting Product API with Docker Compose...

REM Check if Docker is running
docker info >nul 2>&1
if %errorlevel% neq 0 (
    echo Docker is not running. Please start Docker Desktop and try again.
    pause
    exit /b 1
)

REM Build and start the application
echo Building Docker image...
docker-compose build

echo Starting services...
docker-compose up -d

if %errorlevel% equ 0 (
    echo ⏳ Waiting for services to be ready...
    timeout /t 10 /nobreak > nul
    
    echo ✅ Services are running!
    echo.
    echo WITHOUT SECURITY (Port 8080):
    echo   🌐 Application: http://localhost:8080/api
    echo   📊 Swagger UI: http://localhost:8080/api/swagger-ui.html
    echo   ❤️ Health check: http://localhost:8080/api/actuator/health
    echo   🔒 Security status: http://localhost:8080/api/status/security
    echo.
    echo WITH SECURITY (Port 8081):
    echo   🌐 Application: http://localhost:8081/api
    echo   🔑 Login endpoint: http://localhost:8081/api/auth/login
    echo   📊 Swagger UI: http://localhost:8081/api/swagger-ui.html (Admin only)
    echo   ❤️ Health check: http://localhost:8081/api/actuator/health
    echo   🔒 Security status: http://localhost:8081/api/status/security
    echo.
    echo Test users:
    echo   👤 Admin: admin / admin123
    echo   👤 User: user / user123
    echo.
    echo 🔧 Redis Commander: http://localhost:8081
    echo.
    echo To view logs: docker-compose logs -f
    echo To stop: docker-compose down
) else (
    echo ❌ Failed to start services!
    exit /b 1
)
