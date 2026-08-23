# Stage 1: build the application with the Maven wrapper
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy wrapper and POM first to cache dependency resolution
COPY .mvn .mvn
COPY mvnw mvnw.cmd pom.xml ./
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw && ./mvnw -B dependency:go-offline

# Copy source and build the jar
COPY src src
RUN sed -i 's/\r$//' mvnw && ./mvnw -B -DskipTests package

# Stage 2: slim JRE runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
