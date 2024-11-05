FROM openjdk:17-jdk-slim
WORKDIR /app
COPY build/libs/core-service.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]