# Base image
FROM eclipse-temurin:17-jdk

# Install netcat for waiting MySQL
RUN apt-get update && apt-get install -y netcat-openbsd && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy any JAR from target folder (handles versioned filenames automatically)
COPY target/Backend-0.0.1-SNAPSHOT.jar app.jar

# Wait for MySQL, then start app
CMD ["sh", "-c", "until nc -z mysql 3306; do echo 'Waiting for MySQL...'; sleep 2; done; echo 'MySQL is up! Starting app...'; java -jar app.jar"]
