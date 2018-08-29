package br.com.mvppoa.kafka.retries.components.impl;

import br.com.mvppoa.kafka.retries.components.ErrorTopicMessageHolderStrategy;
import br.com.mvppoa.kafka.retries.domain.TopicMessageErrorHolder;
import br.com.mvppoa.kafka.retries.domain.TopicMessageHolder;
import br.com.mvppoa.kafka.retries.repositories.TopicMessageErrorHolderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component("ErrorTopicMessageHolderMongoStrategyImpl")
public class ErrorTopicMessageHolderMongoStrategyImpl implements ErrorTopicMessageHolderStrategy {

    private final TopicMessageErrorHolderRepository topicMessageErrorHolderRepository;

    public ErrorTopicMessageHolderMongoStrategyImpl(TopicMessageErrorHolderRepository topicMessageErrorHolderRepository) {
        this.topicMessageErrorHolderRepository = topicMessageErrorHolderRepository;
    }

    @Override
    public Boolean handleMessageError(TopicMessageHolder holder) {
        log.info("Started mongo error handling method");

        TopicMessageErrorHolder topicMessageErrorHolder = new TopicMessageErrorHolder(
                UUID.randomUUID(),
                holder,
                Instant.now()
        );

        try {
            topicMessageErrorHolderRepository.save(topicMessageErrorHolder);
            return true;
        } catch (Exception e) {
            log.error("Failed to save message to mongo. Object: {}. Error message: {}", topicMessageErrorHolder.toString(), e.getMessage(), e);
        }

        return false;
    }

}
