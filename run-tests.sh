#!/bin/bash

echo "================================================"
echo "  Warehouse API - Test Runner with Docker"
echo "================================================"
echo ""

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

PROJECT_DIR="/home/vanhan/ProjectApp/WarhouseApp"

echo -e "${YELLOW}Step 1:${NC} Preparing test environment..."
echo ""

# Pull Java image if not exists
docker pull eclipse-temurin:17-jdk-alpine > /dev/null 2>&1

echo -e "${YELLOW}Step 2:${NC} Running Maven tests in Docker container..."
echo ""
echo "This may take 1-2 minutes (downloading dependencies on first run)..."
echo ""

# Run tests in Docker
docker run --rm \
  -v "${PROJECT_DIR}":/app \
  -w /app \
  eclipse-temurin:17-jdk-alpine \
  sh -c "./mvnw test"

TEST_EXIT_CODE=$?

echo ""
echo "================================================"

if [ $TEST_EXIT_CODE -eq 0 ]; then
  echo -e "${GREEN}[PASS] ALL TESTS PASSED${NC}"
else
  echo -e "${RED}[FAIL] TESTS FAILED${NC}"
  echo "Check the output above for details."
fi

echo "================================================"
echo ""

exit $TEST_EXIT_CODE
