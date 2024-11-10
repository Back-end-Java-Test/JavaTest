# 1. Base image
FROM openjdk:17-jdk-slim

# 2. Set the working directory in the container
WORKDIR /app

# 3. Copy the build output (JAR file) into the container
COPY build/libs/JavaTest-0.0.1-SNAPSHOT.jar app.jar

# 4. Expose the port the app runs on
EXPOSE 8080

# 5. Run the application directly (no wait-for-it)
CMD ["java", "-jar", "app.jar"]
