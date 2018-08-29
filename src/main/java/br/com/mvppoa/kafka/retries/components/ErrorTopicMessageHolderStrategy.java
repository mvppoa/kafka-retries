package br.com.mvppoa.kafka.retries.components;

import br.com.mvppoa.kafka.retries.domain.TopicMessageHolder;

public interface ErrorTopicMessageHolderStrategy {

    Boolean handleMessageError(TopicMessageHolder holder);

}
