package br.com.mvppoa.kafka.retries.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class Sender{

    private final KafkaTemplate<String, String> kafkaTemplate;

    public Sender(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String topic, String sepBody, Map<String,String> headers) {
        log.debug("Sending topic {} message {} with headers {}", topic,sepBody,headers);

        MessageBuilder messageBuilder =
                MessageBuilder.withPayload(sepBody).setHeader(KafkaHeaders.TOPIC, topic);
        headers.forEach(messageBuilder::setHeader);

        kafkaTemplate.send(messageBuilder.build());
    }

}
