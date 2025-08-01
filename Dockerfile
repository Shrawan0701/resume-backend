# Stage 1: Build the application
FROM openjdk:17-jdk-slim as builder

# Set the working directory
WORKDIR /app

# Copy the Maven wrapper, .mvn directory, and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make the Maven wrapper script executable
RUN chmod +x mvnw

# Download dependencies (this step is cached unless pom.xml changes)
RUN ./mvnw dependency:go-offline

# Copy the rest of the application source code
COPY src ./src

# Build the Spring Boot application
RUN ./mvnw clean install -DskipTests

# Stage 2: Create the final image
FROM eclipse-temurin:17-jre-jammy

# Set the working directory
WORKDIR /app

#
COPY --from=builder /app/target/airesumescanner-0.0.1-SNAPSHOT.jar airesumescanner-0.0.1-SNAPSHOT.jar

# Expose the port your Spring Boot app listens on (default is 8080)
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "airesumescanner-0.0.1-SNAPSHOT.jar"]