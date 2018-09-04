package br.com.mvppoa.kafka.retries.components.impl;

import br.com.mvppoa.kafka.retries.components.ErrorTopicMessageHolderStrategy;
import br.com.mvppoa.kafka.retries.domain.TopicMessageHolder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;

import static org.mockito.Mockito.*;

public class ErrorTopicMessageHolderFileStrategyImplTest {

    private static final String DEFAULT_ERROR_FILE_PATH = "./errors";

    @Mock
    ObjectMapper objectMapper;

    ErrorTopicMessageHolderStrategy errorTopicMessageHolderFileStrategyImpl;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        errorTopicMessageHolderFileStrategyImpl = new ErrorTopicMessageHolderFileStrategyImpl(objectMapper, DEFAULT_ERROR_FILE_PATH);
    }

    @Test
    public void testHandleMessageError() throws Exception {

        ObjectMapper testObjectMaper = new ObjectMapper();

        String messageTopic = "messageTopic";

        TopicMessageHolder topicMessageHolder = new TopicMessageHolder(null, "messagePayload", new HashMap<String, String>() {{
            put("Key", "Value");
        }}, messageTopic, 0, LocalDateTime.of(2018, Month.AUGUST, 28, 15, 24, 27).toInstant(ZoneOffset.UTC));

        when(objectMapper.writeValueAsString(any())).thenReturn(testObjectMaper.writeValueAsString(topicMessageHolder));

        Boolean result = errorTopicMessageHolderFileStrategyImpl.handleMessageError(topicMessageHolder);
        Assert.assertEquals(Boolean.TRUE, result);

        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("Error"){});

        result = errorTopicMessageHolderFileStrategyImpl.handleMessageError(topicMessageHolder);
        Assert.assertEquals(Boolean.TRUE, result);

        Path path = Paths.get(DEFAULT_ERROR_FILE_PATH + "/" + messageTopic + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".json");

        try(AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            Assert.assertTrue(fileChannel.size() > 0);
        }

        Files.delete(path);

    }
}