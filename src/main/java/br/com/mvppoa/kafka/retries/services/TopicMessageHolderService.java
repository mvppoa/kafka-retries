package br.com.mvppoa.kafka.retries.services;

import org.apache.kafka.common.header.Headers;

public interface TopicMessageHolderService {

    void saveTopicMessageHolder(String topic, String message, Headers recordHeaders);

    void searchSendAndDeleteDelayedMessages();

}
