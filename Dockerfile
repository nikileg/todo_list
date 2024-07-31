# Use an official OpenJDK 8 image as the base
FROM sbtscala/scala-sbt:eclipse-temurin-17.0.4_1.7.1_3.2.0

# Set the working directory to /app
WORKDIR /app

# Copy the project files
COPY . /app

# Compile the project
RUN sbt -J-Xmx400m compile

# Expose the port for the application
EXPOSE 8080 443

# Set the command to run the application
CMD ["sbt", "-J-Xmx400m", "run"]
