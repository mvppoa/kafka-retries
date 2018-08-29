package br.com.mvppoa.kafka.retries.components.impl;

import br.com.mvppoa.kafka.retries.components.ErrorTopicMessageHolderStrategy;
import br.com.mvppoa.kafka.retries.domain.TopicMessageErrorHolder;
import br.com.mvppoa.kafka.retries.domain.TopicMessageHolder;
import br.com.mvppoa.kafka.retries.repositories.TopicMessageErrorHolderRepository;
import com.mongodb.MongoClientException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.UUID;
import java.util.logging.Logger;

public class ErrorTopicMessageHolderMongoStrategyImplTest {

    @Mock
    Logger logger;

    @Mock
    TopicMessageErrorHolderRepository topicMessageErrorHolderRepository;

    ErrorTopicMessageHolderStrategy errorTopicMessageHolderStrategy;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        errorTopicMessageHolderStrategy = new ErrorTopicMessageHolderMongoStrategyImpl(topicMessageErrorHolderRepository);
    }

    @Test
    public void handleMessageErrorSuccess() {
        //given
        TopicMessageHolder topicMessageHolder = new TopicMessageHolder();
        topicMessageHolder.setId(UUID.randomUUID());

        //when
        Mockito.when(topicMessageErrorHolderRepository.save(Mockito.any(TopicMessageErrorHolder.class))).thenReturn(new TopicMessageErrorHolder());
        Boolean wasSuccessful = errorTopicMessageHolderStrategy.handleMessageError(topicMessageHolder);

        //then
        Mockito.verify(topicMessageErrorHolderRepository,Mockito.times(1)).save(Mockito.any(TopicMessageErrorHolder.class));
        Assert.assertTrue(wasSuccessful);
    }

    @Test
    public void handleMessageErrorFailure() {
        //given
        TopicMessageHolder topicMessageHolder = new TopicMessageHolder();
        topicMessageHolder.setId(UUID.randomUUID());

        //when
        Mockito.when(topicMessageErrorHolderRepository.save(Mockito.any(TopicMessageErrorHolder.class))).thenThrow(new MongoClientException("Connection failure"));
        Boolean wasSuccessful = errorTopicMessageHolderStrategy.handleMessageError(topicMessageHolder);

        //tehn
        Assert.assertFalse(wasSuccessful);

    }

}