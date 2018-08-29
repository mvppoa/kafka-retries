package br.com.mvppoa.kafka.retries.kafka;

import br.com.mvppoa.kafka.retries.config.KafkaConfigurationUtils;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class KafkaConfigHelper {

    private final String bootstrapServers;
    private final String groupId;

    public KafkaConfigHelper(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers,
                             @Value("${spring.kafka.consumer.group-id}") String groupId) {
        this.bootstrapServers = bootstrapServers;
        this.groupId = groupId;
    }

    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(), new StringDeserializer(),
                new StringDeserializer());
    }

    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    private Map<String, Object> producerConfigs() {
        return KafkaConfigurationUtils.getDefaultProducerConfigs(bootstrapServers);
    }

    private Map<String, Object> consumerConfigs() {
        return KafkaConfigurationUtils.getDefaultConsumerConfigs(bootstrapServers, groupId);
    }
}
