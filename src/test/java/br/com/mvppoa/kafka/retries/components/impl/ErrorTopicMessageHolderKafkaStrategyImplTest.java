package br.com.mvppoa.kafka.retries.components.impl;

import br.com.mvppoa.kafka.retries.domain.TopicMessageHolder;
import br.com.mvppoa.kafka.retries.kafka.Sender;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.HashMap;

import static org.mockito.Mockito.*;

public class ErrorTopicMessageHolderKafkaStrategyImplTest {

    @Mock
    Sender sender;

    @InjectMocks
    ErrorTopicMessageHolderKafkaStrategyImpl errorTopicMessageHolderKafkaStrategyImpl;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testHandleMessageError() throws Exception {
        Boolean result = errorTopicMessageHolderKafkaStrategyImpl.handleMessageError(new TopicMessageHolder(null, "messagePayload", new HashMap<String, String>() {{
            put("String", "String");
        }}, "messageTopic", Integer.valueOf(0), LocalDateTime.of(2018, Month.AUGUST, 28, 23, 37, 5).toInstant(ZoneOffset.UTC)));
        Assert.assertEquals(Boolean.TRUE, result);
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme