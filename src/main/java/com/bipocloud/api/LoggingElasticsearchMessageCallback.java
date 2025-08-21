package com.bipocloud.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingElasticsearchMessageCallback implements ElasticsearchMessageCallback {
    private static final Logger logger = LoggerFactory.getLogger(LoggingElasticsearchMessageCallback.class);
    private final ObjectMapper objectMapper;

    public LoggingElasticsearchMessageCallback(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void handle(ElasticsearchMessage message) {
        try {
            logger.info(objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            logger.warn(e.getMessage());
        }
    }
}
