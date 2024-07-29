# Use an official OpenJDK 8 image as the base
FROM openjdk:8

# Set the working directory to /app
WORKDIR /app

# Copy the project files
COPY . /app

# Install sbt
RUN curl -L -o sbt.deb https://dl.bintray.com/sbt/debian/sbt.deb && dpkg -i sbt.deb && rm sbt.deb

# Compile the project
RUN sbt compile

# Expose the port for the application
EXPOSE 9000

# Set the command to run the application
CMD ["sbt", "run"]
