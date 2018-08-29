package br.com.mvppoa.kafka.retries.services.impl;

import br.com.mvppoa.kafka.retries.kafka.KafkaConfigHelper;
import br.com.mvppoa.kafka.retries.services.KafkaTopicManagerService;
import br.com.mvppoa.kafka.retries.services.TopicMessageHolderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.PartitionInfo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class KafkaTopicManagerServiceImpl implements KafkaTopicManagerService {

    private final Consumer<String, String> consumer;
    private final TopicMessageHolderService topicMessageHolderService;
    private final String delayedQueueSuffix;
    private final Map<String, ConcurrentMessageListenerContainer<String, String>> existingKafkaConsumers;
    private final KafkaConfigHelper kafkaConfigHelper;


    public KafkaTopicManagerServiceImpl(TopicMessageHolderService topicMessageHolderService,
                                        @Qualifier("kafkaTopicManagerConsumer") Consumer<String, String> consumer,
                                        @Value("${application.kafka.delayed-queue-suffix}") String delayedQueueSuffix,
                                        KafkaConfigHelper kafkaConfigHelper) {
        this.consumer = consumer;
        this.delayedQueueSuffix = delayedQueueSuffix;
        this.existingKafkaConsumers = new HashMap<>();
        this.topicMessageHolderService = topicMessageHolderService;
        this.kafkaConfigHelper = kafkaConfigHelper;
    }

    @Override
    public void watchKafkaRetriesTopics() {

        log.debug("Started watchKafkaRetriesTopics method");

        Map<String, List<PartitionInfo>> topics = consumer.listTopics();

        topics.forEach((key, value) -> {
            if (key.endsWith(delayedQueueSuffix) && !existingKafkaConsumers.containsKey(key)) {

                log.debug("Topic {} found. Adding a listener to it.", key);

                ContainerProperties containerProperties = new ContainerProperties(key);
                containerProperties.setMessageListener(
                        (MessageListener<Integer, String>) this::execMessageListener
                );

                ConcurrentMessageListenerContainer<String, String> container =
                        new ConcurrentMessageListenerContainer<>(kafkaConfigHelper.consumerFactory(),
                                containerProperties);

                existingKafkaConsumers.put(key, container);
                container.start();
                log.debug("Topic {} found. Adding a listener to it.", key);
            }

        });
    }

    private void execMessageListener(ConsumerRecord<Integer, String> message) {
        log.debug("Received: {}" + message);
        topicMessageHolderService.saveTopicMessageHolder(
                message.topic().substring(0, message.topic().lastIndexOf(delayedQueueSuffix)),
                message.value(), message.headers());
    }
}
