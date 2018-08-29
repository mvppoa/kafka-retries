package br.com.mvppoa.kafka.retries.components.impl;

import br.com.mvppoa.kafka.retries.components.ErrorStrategyHandler;
import br.com.mvppoa.kafka.retries.components.ErrorTopicMessageHolderStrategy;
import br.com.mvppoa.kafka.retries.domain.TopicMessageHolder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.SQLOutput;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;

public class ErrorStrategyHandlerImplTest {

    @Mock
    ErrorTopicMessageHolderStrategy errorTopicMessageHolderFileStrategy;

    @Mock
    ErrorTopicMessageHolderStrategy errorTopicMessageHolderKafkaStrategy;

    @Mock
    ErrorTopicMessageHolderStrategy errorTopicMessageHolderMongoStrategy;

    List<List<String>> propertiesList = new ArrayList<>();

    Map<ErrorTopicMessageHolderStrategy, Integer> whatToTest = new HashMap<>();

    @Mock
    TopicMessageHolder topicMessageHolder;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        List<String> auxList = new ArrayList<>();
        auxList.add("ALL");
        auxList.add("KAFKA");
        auxList.add("MONGO");
        auxList.add("FILE");

        for (int i = 1; i <= auxList.size(); i++)
            propertiesList.addAll(combination(auxList, i));

        whatToTest.put(errorTopicMessageHolderFileStrategy, 0);
        whatToTest.put(errorTopicMessageHolderKafkaStrategy, 0);
        whatToTest.put(errorTopicMessageHolderMongoStrategy, 0);
    }

    @Test
    public void handleErrors() {

        propertiesList.forEach(strings -> {

            if (strings.contains("ALL") || strings.contains("FILE")) {
                whatToTest.put(errorTopicMessageHolderFileStrategy, whatToTest.get(errorTopicMessageHolderFileStrategy) + 1);
            }
            if (strings.contains("ALL") || strings.contains("KAFKA")) {
                whatToTest.put(errorTopicMessageHolderKafkaStrategy, whatToTest.get(errorTopicMessageHolderKafkaStrategy) + 1);
            }
            if (strings.contains("ALL") || strings.contains("MONGO")) {
                whatToTest.put(errorTopicMessageHolderMongoStrategy, whatToTest.get(errorTopicMessageHolderMongoStrategy) + 1);
            }

            System.out.println("Checking: " + strings);

            ErrorStrategyHandler errorStrategyHandler = new ErrorStrategyHandlerImpl(
                    errorTopicMessageHolderFileStrategy, errorTopicMessageHolderKafkaStrategy,
                    errorTopicMessageHolderMongoStrategy, String.join(",", strings));

            errorStrategyHandler.handleError(topicMessageHolder);

            whatToTest.forEach((errorTopicMessageHolderStrategy, executionTimes) -> {
                Mockito.verify(errorTopicMessageHolderStrategy, times(executionTimes)).handleMessageError(topicMessageHolder);
            });

        });

    }

    private <T> List<List<T>> combination(List<T> values, int size) {

        if (0 == size) {
            return Collections.singletonList(Collections.<T>emptyList());
        }

        if (values.isEmpty()) {
            return Collections.emptyList();
        }

        List<List<T>> combination = new LinkedList<List<T>>();

        T actual = values.iterator().next();

        List<T> subSet = new LinkedList<T>(values);
        subSet.remove(actual);

        List<List<T>> subSetCombination = combination(subSet, size - 1);

        for (List<T> set : subSetCombination) {
            List<T> newSet = new LinkedList<T>(set);
            newSet.add(0, actual);
            combination.add(newSet);
        }

        combination.addAll(combination(subSet, size));

        return combination;
    }
}