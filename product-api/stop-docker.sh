#!/bin/bash
echo "🛑 Stopping Product API services..."

# Stop and remove containers
docker-compose down

echo "✅ Services stopped successfully!"
