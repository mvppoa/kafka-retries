package br.com.mvppoa.kafka.retries.components.impl;

import br.com.mvppoa.kafka.retries.components.ErrorTopicMessageHolderStrategy;
import br.com.mvppoa.kafka.retries.domain.TopicMessageHolder;
import br.com.mvppoa.kafka.retries.kafka.Sender;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.kafka.KafkaException;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.HashMap;

import static org.mockito.Mockito.*;

public class ErrorTopicMessageHolderKafkaStrategyImplTest {

    @Mock
    Sender sender;

    ErrorTopicMessageHolderStrategy errorTopicMessageHolderKafkaStrategyImpl;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        errorTopicMessageHolderKafkaStrategyImpl = new ErrorTopicMessageHolderKafkaStrategyImpl(sender);
    }

    @Test
    public void testHandleMessageError() throws Exception {
        Boolean result = errorTopicMessageHolderKafkaStrategyImpl.handleMessageError(new TopicMessageHolder(null, "messagePayload", new HashMap<String, String>() {{
            put("String", "String");
        }}, "messageTopic", 0, LocalDateTime.of(2018, Month.AUGUST, 28, 23, 37, 5).toInstant(ZoneOffset.UTC)));
        Assert.assertEquals(Boolean.TRUE, result);
    }

    @Test
    public void testHandleMessageErrorFailure() throws Exception {

        doThrow(new KafkaException("Mock Exception")).when(sender).send(any(), any(), any());

        Boolean result = errorTopicMessageHolderKafkaStrategyImpl.handleMessageError(new TopicMessageHolder(null, "messagePayload", new HashMap<String, String>() {{
            put("String", "String");
        }}, "messageTopic", 0, LocalDateTime.of(2018, Month.AUGUST, 28, 23, 37, 5).toInstant(ZoneOffset.UTC)));
        Assert.assertEquals(Boolean.FALSE, result);

    }


}

