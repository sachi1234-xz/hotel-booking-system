FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY target/*.jar app.jar
ENV SPRING_PROFILES_ACTIVE=postgres
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]