package com.kafka.streams;

import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.ValueMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamsApp {
    private static final Logger logger = LoggerFactory.getLogger(KafkaStreamsExample.class);
    public static void main(String[] args) {
        String bootstrapServers=System.getenv("BOOTSTRAP_SERVERS");
        String apiKey = System.getenv("API_KEY");
        String apiSecret = System.getenv("API_SECRET");
        if( apiKey == null || apiSecret == null){
            logger.error("api key and api secret is not set");
            System.exit(1);
        }
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-example");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(StreamsConfig.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        props.put("sasl.mechanism", "PLAIN");
        props.put("sasl.jaas.config", 
            String.format("org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s\" password=\"%s\";",apiKey,apiSecret));
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        StreamsBuilder builder = new StreamsBuilder();

        // Read from input topic
        KStream<String, String> source = builder.stream("input-topic");

        // Perform a simple transformation
        KStream<String, String> transformed = source.mapValues((ValueMapper<String, String>) String::toUpperCase);

        // Write to output topic
        transformed.to("output-topic");

        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        
        // Add a state listener to log state changes
        streams.setStateListener((newState, oldState) -> logger.info("State changing from {} to {}", oldState, newState));

        streams.start();
        logger.info("Kafka Streams application started");

        // Add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down Kafka Streams application");
            streams.close();
            logger.info("Kafka Streams application shut down");
        }));
    }
}
