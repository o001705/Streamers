# Use the latest Ubuntu image as the base
FROM ubuntu:latest

# Set the maintainer label
LABEL maintainer="rkappagantu@gmail.com"

# Update the package list and install necessary packages
RUN apt-get update && apt-get install -y \
    openjdk-11-jre-headless \
    && apt-get clean

# Set the working directory in the container
WORKDIR /app

# Copy the Java class files to the working directory
COPY src/main/java/com/ravik/DirWatcherMain.class /app/

# Specify the command to run the Java application
CMD ["java", "DirWatcherMain"]
