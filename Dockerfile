# Step 1: Use an official Maven image to build the application
FROM maven:3.8.1-openjdk-17 AS build

# Step 2: Set the working directory inside the container
WORKDIR /app

# Step 3: Copy the source code into the container
COPY src /app/src
COPY pom.xml /app

# Step 4: Build the Spring Boot application
RUN mvn clean package

# Step 5: Use an OpenJDK image to run the application
FROM openjdk:17-jdk

# Step 6: Set the working directory for runtime
WORKDIR /app

# Step 7: Copy the built JAR file from the previous stage
COPY --from=build /app/target/*.jar app.jar

# Step 8: Expose the application port
EXPOSE 8080

# Step 9: Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]

