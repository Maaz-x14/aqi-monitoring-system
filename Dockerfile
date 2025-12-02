# --- Stage 1: Build the Application ---
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src ./src

# Build the JAR (Skip tests to speed up deployment)
RUN mvn clean package -DskipTests

# --- Stage 2: Run the Application ---
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port Railway expects
ENV PORT=8080
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]