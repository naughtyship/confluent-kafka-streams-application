package com.kafka.streams;

import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamsApp {
    private static final Logger logger = LoggerFactory.getLogger(StreamsApp.class);
    public static void main(String[] args) {
        String bootstrapServers = System.getenv("BOOTSTRAP_SERVERS");
        String apiKey = System.getenv("API_KEY");
        String apiSecret = System.getenv("API_SECRET");

        if(bootstrapServers == null || apiKey == null || apiSecret == null) {
            logger.error("Environment variables BOOTSTRAP_SERVERS, API_KEY, and API_SECRET must be set");
            System.exit(1);
        }

        Properties props = buildProperties(bootstrapServers, apiKey, apiSecret);
        Topology topology = buidTopology();
        KafkaStreams streams = new KafkaStreams(topology, props);

        streams.setStateListener((newState, oldState) -> {
            logger.info("State changed from {} to {}", oldState, newState);
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down Kafka Streams application");
            streams.close();
        }));

        streams.start();
        logger.info("Kafka Streams application started successfully");
    }

    private static Properties buildProperties(String bootstrapServers, String apiKey, String apiSecret){
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(StreamsConfig.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        props.put("sasl.mechanism", "PLAIN");
        props.put("sasl.jaas.config", 
            String.format("org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s\" password=\"%s\";",apiKey,apiSecret));
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        return props;
    }

    public static Topology buidTopology(){
        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> source = builder.stream("producer-input-topic");
        KStream<String, String> transformed = source.mapValues(value -> value.toUpperCase());
        transformed.to("consumer-topic");
        logger.info("Topology built successfully");
        return builder.build();
    }
}