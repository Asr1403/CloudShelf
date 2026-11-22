
# CloudShelf — Smart Library Management System

CloudShelf is a Spring Boot-based Library Management System with a Docker deployment workflow and Jenkins CI pipeline.

## Features

- Dashboard with library statistics
- Books management
- Members management
- Book issue and return management
- Due-date and overdue tracking
- Book availability tracking
- Persistent database storage

## Technology Stack

| Technology | Purpose |
|------------|---------|
| Java | Backend programming |
| Spring Boot | Web application framework |
| Thymeleaf | Server-side templates |
| Spring Data JPA | Database operations |
| H2 Database | Persistent data storage |
| Maven | Build and dependency management |
| Docker | Containerization |
| Docker Compose | Container deployment |
| Jenkins | Continuous Integration |
| GitHub | Source code management |

## DevOps Workflow

1. Source code is maintained in GitHub.
2. Jenkins checks out the latest source code.
3. Maven builds and tests the application.
4. Jenkins verifies the generated JAR file.
5. A local PowerShell script performs Docker deployment.
6. Docker Compose runs CloudShelf with persistent H2 storage.

## Run Locally

Requirements:
- Java 21
- Docker Desktop

Clone the repository:

```bash
git clone https://github.com/Asr1403/CloudShelf.git
cd CloudShelf
```

Build the application on Windows:

```powershell
.\mvnw.cmd clean verify
```

Start using Docker:

```powershell
docker compose up -d --build
```

Open:

http://localhost:8081

## Windows Deployment Script

Run:

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\deploy.ps1
```

The script checks Docker, verifies the existing database volume, runs Maven tests, and deploys CloudShelf.

**Note:** The deployment script requires an existing Docker volume named `online-library_library-data`.

## Database Persistence

CloudShelf stores its H2 database in a Docker named volume:

`online-library_library-data`

This keeps library records available when the application container is recreated.

Avoid deleting this volume if you want to preserve existing records.

## Jenkins CI Pipeline

The Jenkins pipeline performs:

- Checkout
- Environment verification
- Maven build and tests
- JAR verification

Deployment is currently triggered separately using the local PowerShell script.

## Project Repository

https://github.com/Asr1403/CloudShelf

## Author

Ayush Singh
