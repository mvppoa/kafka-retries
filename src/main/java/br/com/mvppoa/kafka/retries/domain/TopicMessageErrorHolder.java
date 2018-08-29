package br.com.mvppoa.kafka.retries.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Document
public class TopicMessageErrorHolder {

    private static final long serialVersionUID = 8682444181637055261L;

    @Id
    private UUID id;

    @Field
    private TopicMessageHolder topicMessageHolder;

    private Instant creationDate;

}
