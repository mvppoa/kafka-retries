package br.com.mvppoa.kafka.retries.schedulers;

import br.com.mvppoa.kafka.retries.services.TopicMessageHolderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TopicMessageHolderScheduler {

    private final TopicMessageHolderService topicMessageHolderService;

    public TopicMessageHolderScheduler(TopicMessageHolderService topicMessageHolderService) {
        this.topicMessageHolderService = topicMessageHolderService;
    }

    /**
     * Method that will start the message retry
     */
    @Scheduled(cron = "${application.scheduler.execution.topic-message-holder.cron}")
    public void execute() {
        log.info("Started TopicMessageHolderScheduler job");
        try {
            topicMessageHolderService.searchSendAndDeleteDelayedMessages();
        } catch (Exception e) {
            log.error("Could not execute TopicMessageHolderScheduler. Error {}", e);
        }
        log.info("Ended TopicMessageHolderScheduler job");
    }
}
