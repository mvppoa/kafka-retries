package br.com.mvppoa.kafka.retries.components.impl;

import br.com.mvppoa.kafka.retries.components.ErrorTopicMessageHolderStrategy;
import br.com.mvppoa.kafka.retries.domain.TopicMessageHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

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
    ErrorTopicMessageHolderStrategy errorTopicMessageHolderFileStrategyImpl;

    @Before
    public void setUp() {
        errorTopicMessageHolderFileStrategyImpl = new ErrorTopicMessageHolderFileStrategyImpl(new ObjectMapper(), DEFAULT_ERROR_FILE_PATH);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testHandleMessageError() throws Exception {

        String messageTopic = "messageTopic";

        Boolean result = errorTopicMessageHolderFileStrategyImpl.handleMessageError(new TopicMessageHolder(null, "messagePayload", new HashMap<String, String>() {{
            put("String", "String");
        }}, messageTopic, Integer.valueOf(0), LocalDateTime.of(2018, Month.AUGUST, 28, 15, 24, 27).toInstant(ZoneOffset.UTC)));
        Assert.assertEquals(Boolean.TRUE, result);

        result = errorTopicMessageHolderFileStrategyImpl.handleMessageError(new TopicMessageHolder(null, "messagePayload", new HashMap<String, String>() {{
            put("String", "String");
        }}, messageTopic, Integer.valueOf(0), LocalDateTime.of(2018, Month.AUGUST, 28, 15, 24, 27).toInstant(ZoneOffset.UTC)));
        Assert.assertEquals(Boolean.TRUE, result);

        Path path = Paths.get(new StringBuilder().append(DEFAULT_ERROR_FILE_PATH).append("/").append(messageTopic).append(new SimpleDateFormat("yyyyMMdd").format(new Date())).append(".json").toString());

        try(AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            Assert.assertTrue(fileChannel.size() > 0);
        }

        Files.delete(path);

    }
}