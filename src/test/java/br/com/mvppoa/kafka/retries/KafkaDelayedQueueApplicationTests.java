package br.com.mvppoa.kafka.retries;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringRunner.class)
@EmbeddedKafka
@DataMongoTest
@WebAppConfiguration
@SpringBootTest
public class KafkaDelayedQueueApplicationTests {

	@Test
	public void contextLoads() {
	}

}
