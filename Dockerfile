FROM openjdk:11-jre-slim

# Set the working directory inside the container
WORKDIR /app

# Add the JAR file to the container
ADD kafka-kstreams-ref-1.0-SNAPSHOT-jar-with-dependencies.jar /app/my-app.jar

# Create a directory for logs
RUN mkdir -p /app/logs

# Set the command to run the JAR file
CMD [ "java" ,"-jar","my-app.jar"]