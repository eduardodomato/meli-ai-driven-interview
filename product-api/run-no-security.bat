@echo off
echo 🚀 Starting Product API WITHOUT Security...

REM Check if Java is available
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Java is not installed or not in PATH. Please install Java 21 and try again.
    pause
    exit /b 1
)

REM Build the application
echo Building application...
call mvnw clean package -DskipTests

if %errorlevel% neq 0 (
    echo ❌ Build failed!
    pause
    exit /b 1
)

echo Starting application without security...
java -jar target/product-api-0.0.1-SNAPSHOT.jar --spring.profiles.active=no-security

pause
