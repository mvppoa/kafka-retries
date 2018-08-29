package br.com.mvppoa.kafka.retries.schedulers;

import br.com.mvppoa.kafka.retries.services.KafkaTopicManagerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaTopicManagerScheduler {

    private final KafkaTopicManagerService kafkaTopicManagerService;

    public KafkaTopicManagerScheduler(KafkaTopicManagerService kafkaTopicManagerService) {
        this.kafkaTopicManagerService = kafkaTopicManagerService;
    }

    /**
     * Method that will search for existing retry topics and add a listener to them.
     */
    @Scheduled(cron = "${application.scheduler.execution.kafka-topic-manager.cron}")
    public void execute() {
        log.info("Started KafkaTopicManagerScheduler job");
        try {
            kafkaTopicManagerService.watchKafkaRetriesTopics();
        } catch (Exception e) {
            log.error("Could not execute KafkaTopicManagerScheduler. Error {}", e);
        }
        log.info("Ended KafkaTopicManagerScheduler job");
    }
}
