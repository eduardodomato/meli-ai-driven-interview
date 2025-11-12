@echo off
echo 🛑 Stopping Product API services...

REM Stop and remove containers
docker-compose down

echo ✅ Services stopped successfully!
