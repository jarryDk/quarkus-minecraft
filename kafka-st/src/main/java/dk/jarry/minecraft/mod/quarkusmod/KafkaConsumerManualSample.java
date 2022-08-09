package dk.jarry.minecraft.mod.quarkusmod;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class KafkaConsumerManualSample {

    public static void main(String[] args) {

        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "localhost:9092");
        props.setProperty("group.id", "test");
        props.setProperty("enable.auto.commit", "false");
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {

            consumer.subscribe(Arrays.asList("quarkus-mod-kafka"));

            final int minBatchSize = 200;

            List<ConsumerRecord<String, String>> buffer = new ArrayList<>();
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));               
                for (ConsumerRecord<String, String> record : records) {
                    buffer.add(record);
                    System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(),record.value());
                }
                if (buffer.size() >= minBatchSize) {
                    // insertIntoDb(buffer);
                    consumer.commitSync();
                    buffer.clear();
                }
            }
        }
    }
}
