package br.com.mvppoa.kafka.retries.schedulers;

import br.com.mvppoa.kafka.retries.services.KafkaTopicManagerService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import static org.mockito.Mockito.*;

public class KafkaTopicManagerSchedulerTest {

    @Mock
    KafkaTopicManagerService kafkaTopicManagerService;

    @InjectMocks
    KafkaTopicManagerScheduler kafkaTopicManagerScheduler;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExecute() throws Exception {
        kafkaTopicManagerScheduler.execute();
    }
}