package br.com.mvppoa.kafka.retries.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Object to keep delayed messages.
 *
 * A TopicMessageHolder.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Document
public class TopicMessageHolder {

	private static final long serialVersionUID = 8682444181637055262L;

	@Id
    private UUID id;

    private String messagePayload;

    private Map<String,String> messageHeader;

    private String messageTopic;

    private Integer retries;

    @Field
    private Instant delayInstant;

}
