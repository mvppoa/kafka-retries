package br.com.mvppoa.kafka.retries.components.impl;

import br.com.mvppoa.kafka.retries.components.ErrorTopicMessageHolderStrategy;
import br.com.mvppoa.kafka.retries.domain.TopicMessageHolder;
import br.com.mvppoa.kafka.retries.components.ErrorStrategyHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ErrorStrategyHandlerImpl implements ErrorStrategyHandler {

    private static final String STRATEGY_KAFKA = "KAFKA";
    private static final String STRATEGY_MONGO = "MONGO";
    private static final String STRATEGY_FILE = "FILE";
    private static final String STRATEGY_ALL = "ALL";

    private final ErrorTopicMessageHolderStrategy errorTopicMessageHolderFileStrategy;
    private final ErrorTopicMessageHolderStrategy errorTopicMessageHolderKafkaStrategy;
    private final ErrorTopicMessageHolderStrategy errorTopicMessageHolderMongoStrategy;

    private final String errorStrategySelector;

    public ErrorStrategyHandlerImpl(
            @Qualifier("ErrorTopicMessageHolderFileStrategyImpl") ErrorTopicMessageHolderStrategy errorTopicMessageHolderFileStrategy,
            @Qualifier("ErrorTopicMessageHolderKafkaStrategyImpl") ErrorTopicMessageHolderStrategy errorTopicMessageHolderKafkaStrategy,
            @Qualifier("ErrorTopicMessageHolderMongoStrategyImpl") ErrorTopicMessageHolderStrategy errorTopicMessageHolderMongoStrategy,
            @Value("${application.error.strategy}") String errorStrategySelector) {
        this.errorTopicMessageHolderFileStrategy = errorTopicMessageHolderFileStrategy;
        this.errorTopicMessageHolderKafkaStrategy = errorTopicMessageHolderKafkaStrategy;
        this.errorTopicMessageHolderMongoStrategy = errorTopicMessageHolderMongoStrategy;
        this.errorStrategySelector = errorStrategySelector.toUpperCase();
    }

    @Override
    public void handleError(TopicMessageHolder topicMessageHolder) {

        Boolean sendKafka = errorStrategySelector.contains(STRATEGY_ALL) || errorStrategySelector.contains(STRATEGY_KAFKA);
        Boolean sendMongo = errorStrategySelector.contains(STRATEGY_ALL) || errorStrategySelector.contains(STRATEGY_MONGO);
        Boolean sendFile = errorStrategySelector.contains(STRATEGY_ALL) || errorStrategySelector.contains(STRATEGY_FILE);

        if (sendMongo) {
            errorTopicMessageHolderMongoStrategy.handleMessageError(topicMessageHolder);
        }

        if (sendKafka) {
            errorTopicMessageHolderKafkaStrategy.handleMessageError(topicMessageHolder);
        }

        if (sendFile) {
            errorTopicMessageHolderFileStrategy.handleMessageError(topicMessageHolder);
        }

    }
}
