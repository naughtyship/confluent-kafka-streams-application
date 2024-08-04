# Kafka Streams Application

## Overview

This document outlines the structure, configuration, and deployment process for the Kafka Streams application. The application reads messages from a Kafka topic, processes the messages by converting specific fields to uppercase, and then writes the processed messages to another Kafka topic. The application leverages Confluent Schema Registry for managing Avro schemas and includes robust logging for monitoring and troubleshooting.

## Application Structure

### KafkaStreamsExample Class

- The main class of the application.
- Configures the Kafka Streams properties, including connection details to Kafka brokers and the schema registry.
- Defines the stream processing topology using `StreamsBuilder`.

### Stream Processing Logic

- Reads from an input Kafka topic.
- Applies a transformation to convert certain fields to uppercase.
- Writes the transformed records to an output Kafka topic.

### Logging

- Uses SLF4J and Log4j for logging.
- Logs key application events, such as state changes and errors.

### Configuration

- Reads configuration details from environment variables.
- Includes settings for Kafka brokers, security protocols, and schema registry.

### Exception Handling

- Configures an uncaught exception handler to log and manage runtime exceptions.
- Adds a state change listener to log Kafka Streams state transitions.

### Environment Variables

Set up environment variables for:
- `BOOTSTRAP_SERVERS_CONFIG`
- `YOUR_API_KEY`
- `YOUR_API_SECRET`
- `SCHEMA_REGISTRY_URL`
- `SCHEMA_REGISTRY_KEY`
- `SCHEMA_REGISTRY_SECRET`

### Schema Registry Configuration

- Ensure the Confluent Schema Registry is configured and accessible.
- Define the Avro schema for the Kafka topics.

### Dockerfile

- The Dockerfile for the application should be defined to create a Docker image with the necessary dependencies and configurations.

### Dependencies

- Include dependencies for Kafka Streams, Confluent Schema Registry, and Avro in the `pom.xml` file for Maven.