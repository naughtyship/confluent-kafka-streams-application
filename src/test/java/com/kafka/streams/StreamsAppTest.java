package com.kafka.streams;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.StreamsConfig;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Properties;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static com.github.stefanbirkner.systemlambda.SystemLambda.*;

public class StreamsAppTest {

    private TopologyTestDriver testDriver;
    private TestInputTopic<String, String> inputTopic;
    private TestOutputTopic<String, String> outputTopic;

    @BeforeEach
    public void setup() {
        // Build the topology
        Topology topology = StreamsApp.buidTopology();

        // Define minimal properties for the test driver
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "test-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        testDriver = new TopologyTestDriver(topology, props);

        // Define test topics
        inputTopic = testDriver.createInputTopic(
                "producer-input-topic",
                Serdes.String().serializer(),
                Serdes.String().serializer()
        );

        outputTopic = testDriver.createOutputTopic(
                "consumer-topic",
                Serdes.String().deserializer(),
                Serdes.String().deserializer()
        );
    }

    @AfterEach
    public void tearDown() {
        testDriver.close();
    }

    @Test
    public void testUppercaseTransformation() {
        inputTopic.pipeInput("key1", "hello world");

        List<String> values = outputTopic.readValuesToList();

        assertEquals(1, values.size());
        assertEquals("HELLO WORLD", values.get(0));
    }
    @Test
    public void testBuildProperties() {
        Properties props = StreamsApp.buildProperties("localhost:9092", "test-key", "test-secret");
        assertEquals("streams-app", props.getProperty(StreamsConfig.APPLICATION_ID_CONFIG));
        assertEquals("localhost:9092", props.getProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertEquals("SASL_SSL", props.getProperty(StreamsConfig.SECURITY_PROTOCOL_CONFIG));
        assertTrue(props.getProperty("sasl.jaas.config").contains("username=\"test-key\""));
    }
    @Test
    public void testMainMethodWithProperties() {
        System.setProperty("test.env", "true");
        System.setProperty("BOOTSTRAP_SERVERS", "localhost:9092");
        System.setProperty("API_KEY", "test-key");
        System.setProperty("API_SECRET", "test-secret");

        StreamsApp.main(new String[]{});

        System.clearProperty("test.env");
        System.clearProperty("BOOTSTRAP_SERVERS");
        System.clearProperty("API_KEY");
        System.clearProperty("API_SECRET");
    }
    @Test
    public void testRunMethodSkipsKafkaStreamsStartInTestEnv() {
        // Set test system property to simulate test mode
        System.setProperty("test.env", "true");

        // Call the run method directly (bypasses main())
        StreamsApp.run("localhost:9092", "test-key", "test-secret");

        // Clean up
        System.clearProperty("test.env");
    }
    @Disabled("Integration test - requires Kafka broker")
    @Test
    public void testRunMethodStartsKafkaStreams() {
        System.clearProperty("test.env"); // ensures real start

        StreamsApp.run("localhost:9092", "test-key", "test-secret");

        // You'd need to verify stream state externally if you enable this
    }

   
}
