package br.com.mvppoa.kafka.retries.repositories;

import br.com.mvppoa.kafka.retries.domain.TopicMessageHolder;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface TopicMessageHolderRepository extends MongoRepository<TopicMessageHolder, UUID> {

    List<TopicMessageHolder> findAllByDelayInstantLessThanEqual(Instant instant);

    void deleteAllByIdIn(Set<UUID> ids);

}
