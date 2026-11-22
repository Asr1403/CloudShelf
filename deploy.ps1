
# ==================================================
# CloudShelf - Local Windows Docker Deployment
# ==================================================

$ErrorActionPreference = "Stop"

# Always run from the project directory
Set-Location $PSScriptRoot

Write-Host "====================================="
Write-Host " CloudShelf Deployment"
Write-Host "====================================="

# Step 1: Verify Docker is available
Write-Host "`n[1/5] Checking Docker..."

docker info --format '{{.ServerVersion}}'

if ($LASTEXITCODE -ne 0) {
    throw "Docker is not running or is inaccessible."
}

# Step 2: Verify persistent database volume
Write-Host "`n[2/5] Checking database volume..."

$volume = docker volume inspect online-library_library-data 2>$null

if ($LASTEXITCODE -ne 0) {
    throw "Existing database volume was not found. Deployment stopped."
}

Write-Host "Persistent database volume found."

# Step 3: Build application and run tests
Write-Host "`n[3/5] Building and testing CloudShelf..."

& .\mvnw.cmd -B clean verify

if ($LASTEXITCODE -ne 0) {
    throw "Maven build or tests failed. Deployment stopped."
}

# Step 4: Rebuild and deploy Docker container
Write-Host "`n[4/5] Deploying CloudShelf..."

docker compose up -d --build --no-deps library-app

if ($LASTEXITCODE -ne 0) {
    throw "Docker deployment failed."
}

# Step 5: Check container status
Write-Host "`n[5/5] Checking CloudShelf container..."

docker ps --filter "name=online-library-container"

if ($LASTEXITCODE -ne 0) {
    throw "Unable to check container status."
}

Write-Host "`nDeployment command completed."
Write-Host "CloudShelf URL: http://localhost:8081"
Write-Host "Database volume preserved."
