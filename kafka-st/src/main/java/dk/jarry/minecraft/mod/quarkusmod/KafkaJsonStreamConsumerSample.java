package dk.jarry.minecraft.mod.quarkusmod;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.ValueMapper;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * https://github.com/cjolif/streaming-examples/blob/master/kafka/src/main/java/Example.java
 */
public class KafkaJsonStreamConsumerSample {

    public static void main(String[] args) {

        Properties props = new Properties();
        props.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "streams-player-json");
        //props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, JsonDeserializer.class.getName());

        final StreamsBuilder builder = new StreamsBuilder();
        KStream<String, JsonNode> source = builder.stream("kafka-mod-item-stack");

        source.flatMapValues(new ValueMapper<JsonNode, Iterable<JsonNode>>() {
            @Override
            public Iterable<JsonNode> apply(JsonNode value) {
                System.out.println("... . ");
                return Arrays.asList(value.get("player"));
            }
        }).to("kafka-mod-item-stack-player-output");

        final Topology topology = builder.build();

        final KafkaStreams streams = new KafkaStreams(topology, props);
        final CountDownLatch latch = new CountDownLatch(1);

        // attach shutdown handler to catch control-c
        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook-json") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });

        try {
            streams.start();
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);
    }

}
