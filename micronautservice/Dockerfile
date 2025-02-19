FROM eclipse-temurin:17-jdk-focal AS build

WORKDIR /app

# Copy Gradle wrapper and project configuration files first for better caching
COPY gradle/ gradle/
COPY gradlew .  
COPY build.gradle .  
COPY settings.gradle .  
COPY gradle.properties .  

# Ensure correct permissions and fix line endings
RUN apt-get update && apt-get install -y dos2unix
RUN dos2unix gradlew
RUN chmod +x gradlew

# Verify file permissions and working directory
RUN ls -la
RUN pwd

# Download dependencies
RUN ./gradlew --version

# Copy the rest of the source code
COPY src/ src/

# Build the application
RUN ./gradlew build -x test

FROM eclipse-temurin:17-jre-focal

WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
