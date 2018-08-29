package br.com.mvppoa.kafka.retries.config;

import br.com.mvppoa.kafka.retries.kafka.KafkaConfigHelper;
import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
public class ReceiverConfiguration {

    private final KafkaConfigHelper kafkaConfigHelper;

    public ReceiverConfiguration(KafkaConfigHelper kafkaConfigHelper) {
        this.kafkaConfigHelper = kafkaConfigHelper;
    }

    @Bean(value = "kafkaTopicManagerConsumer")
    public Consumer<String, String> kafkaConsumer() {
        return kafkaConfigHelper.consumerFactory().createConsumer();
    }
}
