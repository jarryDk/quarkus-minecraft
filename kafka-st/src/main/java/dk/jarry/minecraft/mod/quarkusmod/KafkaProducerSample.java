package dk.jarry.minecraft.mod.quarkusmod;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.errors.TopicExistsException;
import org.apache.kafka.common.serialization.StringSerializer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * https://github.com/confluentinc/examples/tree/7.2.1-post/clients/cloud/java/src/main/java/io/confluent/examples/clients/cloud
 */
public class KafkaProducerSample {

    public static void main(final String[] args) throws IOException {

        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");        
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());

        // Create topic if needed
        final String topic = "quarkus-mod-kafka";
        createTopic(topic, props);

        Producer<String, JsonNode> producer = new KafkaProducer<String, JsonNode>(props);

        final ObjectMapper objectMapper = new ObjectMapper();

        // Produce sample data
        final Long numMessages = 10L;
        for (Long i = 0L; i < numMessages; i++) {
            String key = "alice c";
            JsonNode record = objectMapper.valueToTree(
                new DataRecord(i, key, ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT)));

            System.out.printf("Producing record: %s\t%s%n", key, record);
            producer.send(new ProducerRecord<String, JsonNode>(topic, key, record), new Callback() {
                @Override
                public void onCompletion(RecordMetadata m, Exception e) {
                    if (e != null) {
                        e.printStackTrace();
                    } else {
                        System.out.printf("Produced record to topic %s partition [%d] @ offset %d%n", m.topic(),
                                m.partition(), m.offset());
                    }
                }
            });
        }

        producer.flush();

        System.out.printf(numMessages + " messages were produced to topic %s%n", topic);

        producer.close();

    }

    public static void createTopic(final String topic, final Properties cloudConfig) {
        final NewTopic newTopic = new NewTopic(topic, Optional.empty(), Optional.empty());
        try (final AdminClient adminClient = AdminClient.create(cloudConfig)) {
            adminClient.createTopics(Collections.singletonList(newTopic)).all().get();
        } catch (final InterruptedException | ExecutionException e) {
            // Ignore if TopicExistsException, which may be valid if topic exists
            if (!(e.getCause() instanceof TopicExistsException)) {
                throw new RuntimeException(e);
            }
        }
    }

}
