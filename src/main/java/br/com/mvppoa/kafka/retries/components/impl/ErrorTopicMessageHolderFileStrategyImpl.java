package br.com.mvppoa.kafka.retries.components.impl;

import br.com.mvppoa.kafka.retries.domain.TopicMessageHolder;
import br.com.mvppoa.kafka.retries.components.ErrorTopicMessageHolderStrategy;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Future;

@Slf4j
@Component("ErrorTopicMessageHolderFileStrategyImpl")
public class ErrorTopicMessageHolderFileStrategyImpl implements ErrorTopicMessageHolderStrategy {

    private String defaultErrorFilePath;
    private ObjectMapper objectMapper;

    public ErrorTopicMessageHolderFileStrategyImpl(ObjectMapper objectMapper, @Value("${application.error.strategy.file.default-error-file-path}") String defaultErrorFilePath) {
        this.defaultErrorFilePath = defaultErrorFilePath;
        this.objectMapper = objectMapper;
    }

    @Override
    public Boolean handleMessageError(TopicMessageHolder holder) {

        log.info("Started file error handling method");

        String fileName = holder.getMessageTopic();
        String convertedMessage;

        try {
            convertedMessage = objectMapper.writeValueAsString(holder);
        } catch (JsonProcessingException e) {
            log.error("Could not serialize the object. Error: {}", e);
            log.info("Saving info as custom string instead of json.");
            fileName = fileName + "_custom";
            convertedMessage = String.valueOf(holder.getId()) + '|' + holder.getMessageTopic() +
                    '|' + holder.getMessagePayload() + '|' +
                    holder.getMessageHeader().toString() + '|' +
                    holder.getDelayInstant().toString();
        }

        Path path = Paths.get(new StringBuilder().append(defaultErrorFilePath).append("/").append(fileName).append(new SimpleDateFormat("yyyyMMdd").format(new Date())).append(".json").toString());

        try(AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(path,  StandardOpenOption.CREATE, StandardOpenOption.WRITE)){
            if (!path.toFile().exists()) {
                Files.createFile(path);
            }

            if(fileChannel.size() > 0){
                convertedMessage = ",\n" + convertedMessage;
            }

            ByteBuffer buffer = ByteBuffer.allocate(convertedMessage.getBytes().length);
            buffer.put(convertedMessage.getBytes());
            buffer.flip();

            Future<Integer> operation = fileChannel.write(buffer, fileChannel.size());
            buffer.clear();

            while (!operation.isDone()) ;

            log.info("Saved message {} to file {}.", convertedMessage, fileName);

            return true;

        } catch (IOException e) {
            log.error("Could not write message {} to file {}. Error: {}", convertedMessage, fileName, e.getMessage(),e);
        }

        return false;
    }

}
