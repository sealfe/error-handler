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
    private final StackTraceLocator stackTraceLocator;

    public LoggingElasticsearchMessageCallback(ObjectMapper objectMapper, StackTraceLocator stackTraceLocator) {
        this.objectMapper = objectMapper;
        this.stackTraceLocator = stackTraceLocator;
    }

    public void handle(ElasticsearchMessage message) {
        try {
            StackTraceElement element = stackTraceLocator.locateBusinessFrame(message.getLog().getStack());
            if (element != null) {
                logger.info(objectMapper.writeValueAsString(element));
            } else {
                logger.info(objectMapper.writeValueAsString(message));
            }
        } catch (JsonProcessingException e) {
            logger.warn(e.getMessage());
        }
    }
}
