package br.com.mvppoa.kafka.retries.components.impl;

import br.com.mvppoa.kafka.retries.components.ErrorTopicMessageHolderStrategy;
import br.com.mvppoa.kafka.retries.domain.TopicMessageHolder;
import br.com.mvppoa.kafka.retries.kafka.Sender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("ErrorTopicMessageHolderKafkaStrategyImpl")
public class ErrorTopicMessageHolderKafkaStrategyImpl implements ErrorTopicMessageHolderStrategy {

    private Sender sender;
    private static final String DLQ_SUFFIX = "_DLQ";

    public ErrorTopicMessageHolderKafkaStrategyImpl(Sender sender) {
        this.sender = sender;
    }

    @Override
    public Boolean handleMessageError(TopicMessageHolder topicMessageErrorHolder) {
        log.info("Started kafka error handling method");
        try {
            sender.send(topicMessageErrorHolder.getMessageTopic() + DLQ_SUFFIX,
                    topicMessageErrorHolder.getMessagePayload(), topicMessageErrorHolder.getMessageHeader());
            return true;
        } catch (Exception e) {
            log.error("Failed to send message to kafka. Object: {}. Error message: {}", topicMessageErrorHolder.toString(), e);
        }
        return false;
    }

}
