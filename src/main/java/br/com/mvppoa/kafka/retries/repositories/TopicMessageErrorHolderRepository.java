package br.com.mvppoa.kafka.retries.repositories;

import br.com.mvppoa.kafka.retries.domain.TopicMessageErrorHolder;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TopicMessageErrorHolderRepository extends MongoRepository<TopicMessageErrorHolder, UUID> {

}

