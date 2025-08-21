package com.bipocloud.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingElasticsearchMessageCallback implements ElasticsearchMessageCallback {
    private static final Logger logger = LoggerFactory.getLogger(LoggingElasticsearchMessageCallback.class);
    private final ObjectMapper objectMapper;
    private final StackTraceLocator stackTraceLocator;
    private final CodeRecordBuilder builder;

    public LoggingElasticsearchMessageCallback(ObjectMapper objectMapper, StackTraceLocator stackTraceLocator, CodeRecordBuilder builder) {
        this.objectMapper = objectMapper;
        this.stackTraceLocator = stackTraceLocator;
        this.builder = builder;
    }

    public void handle(ElasticsearchMessage message) {
        try {
            StackTraceRootCause cause = stackTraceLocator.findRootCause(message.getSource().getStackTrace());
            if (cause != null) {
                CodeRecord record = builder.build(cause, message.getSource().getAppName());
                logger.info(objectMapper.writeValueAsString(record));
            } else {
                logger.info(objectMapper.writeValueAsString(message));
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
    }
}
