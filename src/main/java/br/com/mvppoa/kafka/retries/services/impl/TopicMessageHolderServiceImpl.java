package br.com.mvppoa.kafka.retries.services.impl;

import br.com.mvppoa.kafka.retries.repositories.TopicMessageHolderRepository;
import br.com.mvppoa.kafka.retries.components.ErrorStrategyHandler;
import br.com.mvppoa.kafka.retries.domain.TopicMessageHolder;
import br.com.mvppoa.kafka.retries.kafka.Sender;
import br.com.mvppoa.kafka.retries.services.TopicMessageHolderService;
import com.hazelcast.core.HazelcastInstance;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.locks.Lock;

@Service
@Slf4j
public class TopicMessageHolderServiceImpl implements TopicMessageHolderService {

    private static final String HEADER_KR_DELAY = "KR-Delay";
    private static final String HEADER_KR_RETRIES = "KR-Retries";
    private static final Long DELAY_MULTIPLIER = 2L;
    private static final Integer MAX_RETRIES = 5;

    private final TopicMessageHolderRepository topicMessageHolderRepository;
    private final HazelcastInstance hazelcastInstance;
    private final ErrorStrategyHandler errorStrategyHandler;
    private final Sender sender;
    private final Long defaultTimeToDelayMessage;
    private final String headerIgnore;

    public TopicMessageHolderServiceImpl(
            TopicMessageHolderRepository topicMessageHolderRepository,
            ErrorStrategyHandler errorStrategyHandler,
            HazelcastInstance hazelcastInstance,
            Sender sender,
            @Value("${application.kafka.default-time-to-delay-message}") Long defaultTimeToDelayMessage,
            @Value("${application.kafka.header-ignore}") String headerIgnore) {
        this.topicMessageHolderRepository = topicMessageHolderRepository;
        this.defaultTimeToDelayMessage = defaultTimeToDelayMessage;
        this.errorStrategyHandler = errorStrategyHandler;
        this.hazelcastInstance = hazelcastInstance;
        this.sender = sender;
        this.headerIgnore = headerIgnore;
    }

    /**
     * This method will save a kafka message to mongo DB.
     * If the database is unavailable it will save te register into a file that
     * can be consumed by the database later on.
     *
     * @param topic         message topic to be recorded
     * @param message       message to be recorded
     * @param recordHeaders headers to be recorded
     */
    @Override
    public void saveTopicMessageHolder(String topic, String message, Headers recordHeaders) {

        log.info("Saving TopicMessageHolder");

        TopicMessageHolder topicMessageHolder = new TopicMessageHolder();
        Long timeToDelayMessage = defaultTimeToDelayMessage;
        Integer retries = 0;

        topicMessageHolder.setId(UUID.randomUUID());
        topicMessageHolder.setMessagePayload(message);
        topicMessageHolder.setMessageTopic(topic);

        Map<String, String> headerMap = new HashMap<>();
        try {
            for (Header header : recordHeaders) {
                //TODO - Improve header handling to consider header types.
                if(headerIgnore.contains(header.key())){
                    continue;
                }
                headerMap.put(header.key(), new String(header.value(), "UTF-8").replace("\"",""));
                if (HEADER_KR_DELAY.equals(header.key())) {
                    timeToDelayMessage = Math.multiplyExact(Long.valueOf(headerMap.get(header.key())), DELAY_MULTIPLIER);
                    headerMap.put(header.key(), Long.toString(timeToDelayMessage));
                }
                if (HEADER_KR_RETRIES.equals(header.key())) {
                    retries = Integer.valueOf(headerMap.get(header.key())) + 1;
                    headerMap.put(header.key(), Integer.toString(retries));
                }
            }
        } catch (UnsupportedEncodingException e) {
            log.error("Could not unserialize header for message {}. Error: {}", message, e);
        }
        if (Objects.isNull(headerMap.get(HEADER_KR_DELAY))) {
            headerMap.put(HEADER_KR_DELAY, Long.toString(timeToDelayMessage));
        }
        if (Objects.isNull(headerMap.get(HEADER_KR_RETRIES))) {
            headerMap.put(HEADER_KR_RETRIES, Integer.toString(retries));
        }
        topicMessageHolder.setRetries(retries);
        topicMessageHolder.setMessageHeader(headerMap);
        topicMessageHolder.setDelayInstant(Instant.now().plus(timeToDelayMessage, ChronoUnit.MINUTES));

        try {
            if(MAX_RETRIES <= topicMessageHolder.getRetries()){
                log.error("Max retries reached for message {}" ,topicMessageHolder.toString());
                errorStrategyHandler.handleError(topicMessageHolder);
            } else {
                log.info("Saving message: {}",topicMessageHolder.toString());
                topicMessageHolderRepository.save(topicMessageHolder);
            }
        } catch (Exception e) {
            log.error("Could not save message into database. Error: {}", e);
            errorStrategyHandler.handleError(topicMessageHolder);
        }
    }

    /**
     * Method triggered by the scheduler that will search all the messages that will be retried and sends them to a topic.
     * @see br.com.mvppoa.kafka.retries.schedulers.TopicMessageHolderScheduler
     */
    @Override
    public void searchSendAndDeleteDelayedMessages() {
        Instant instant = Instant.now();

        List<TopicMessageHolder> topicMessageHolders = topicMessageHolderRepository.findAllByDelayInstantLessThanEqual(instant);
        Collections.shuffle(topicMessageHolders);

        Map<UUID, Boolean> mapMessage = hazelcastInstance.getMap("existingUUIDs");

        topicMessageHolders.forEach(topicMessageHolder -> sendAndDeleteMessage(mapMessage, topicMessageHolder));

    }

    /**
     * Method that sends and deletes messages from the retry database
     *
     * @param mapMessage Hazelcast map that contains the uuids of the inserted registers. Used to avoid message duplication.
     * @param topicMessageHolder Message to be sent
     */
    private void sendAndDeleteMessage(Map<UUID, Boolean> mapMessage, TopicMessageHolder topicMessageHolder) {

        Lock lock = hazelcastInstance.getLock(topicMessageHolder.getId().toString());
        lock.lock();

        try {

            if (mapMessage.get(topicMessageHolder.getId()) == null) {
                mapMessage.put(topicMessageHolder.getId(), true);

                sender.send(topicMessageHolder.getMessageTopic(),
                        topicMessageHolder.getMessagePayload(),
                        topicMessageHolder.getMessageHeader());

                topicMessageHolderRepository.deleteById(topicMessageHolder.getId());
            } else {
                log.info("Register already sent. Skipping record.");
            }

        } catch (Exception e) {
            log.error("Lock failure. Skiping record. Error: {}", e);
        } finally {
            lock.unlock();
        }
    }

}
