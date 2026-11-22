
FROM eclipse-temurin:21-jre

WORKDIR /app

# Create persistent database directory
RUN mkdir -p /app/data

# Configure H2 database storage
ENV LIBRARY_DB_URL=jdbc:h2:file:/app/data/librarydb

# Copy the Spring Boot application
COPY target/online-library-0.0.1-SNAPSHOT.jar app.jar

# Application port
EXPOSE 8081

# Start Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
