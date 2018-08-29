package br.com.mvppoa.kafka.retries.config;

import br.com.mvppoa.kafka.retries.kafka.KafkaConfigHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@EnableKafka
public class SenderConfiguration {

    private KafkaConfigHelper kafkaConfigHelper;

    public SenderConfiguration(KafkaConfigHelper kafkaConfigHelper) {
        this.kafkaConfigHelper = kafkaConfigHelper;
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(kafkaConfigHelper.producerFactory());
    }

}
