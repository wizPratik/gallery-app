# Use an official Maven image to build the application
FROM maven:3.8.8-eclipse-temurin-17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml and source code into the container
COPY pom.xml .
COPY src ./src

# Download the dependencies and build the application
RUN mvn clean package -DskipTests

# Use an official OpenJDK runtime image for the final build
FROM eclipse-temurin:17-jdk-ubi9-minimal

# Set the working directory inside the container
WORKDIR /app

# Copy the built jar file from the previous stage
COPY --from=build /app/target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
