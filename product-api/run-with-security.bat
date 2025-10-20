@echo off
echo 🔐 Starting Product API WITH Security...

REM Check if Java is available
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Java is not installed or not in PATH. Please install Java 21 and try again.
    pause
    exit /b 1
)

REM Set JWT environment variables
set JWT_SECRET=mySecretKeyForLocalDevelopment
set JWT_EXPIRATION=86400000

REM Build the application
echo Building application...
call mvnw clean package -DskipTests

if %errorlevel% neq 0 (
    echo ❌ Build failed!
    pause
    exit /b 1
)

echo Starting application with security...
echo Test users:
echo   👤 Admin: admin / admin123
echo   👤 User: user / user123
echo.
java -jar target/product-api-0.0.1-SNAPSHOT.jar --spring.profiles.active=security

pause
