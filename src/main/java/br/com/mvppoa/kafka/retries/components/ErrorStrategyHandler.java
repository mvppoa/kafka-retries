package br.com.mvppoa.kafka.retries.components;

import br.com.mvppoa.kafka.retries.domain.TopicMessageHolder;

public interface ErrorStrategyHandler {

    void handleError(TopicMessageHolder topicMessageHolder);

}
