package com.bipocloud.spell.errorhandler.api;

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
    private final CodeAnalyzer analyzer;

    public LoggingElasticsearchMessageCallback(ObjectMapper objectMapper, CodeRecordBuilder builder, CodeAnalyzer analyzer) {
        this.objectMapper = objectMapper;
        this.stackTraceLocator = new StackTraceLocator();
        this.builder = builder;
        this.analyzer = analyzer;
    }

    public void handle(ElasticsearchMessage message) {
        try {
            String stack = message.extractStackTrace();
            if (stack != null) {
                StackTraceRootCause cause = stackTraceLocator.findRootCause(stack);
                if (cause != null) {
                    CodeRecord record = builder.build(cause, message.extractAppName(), stack);
                    String result = analyzer.analyze(record);
                    return;
                }
            }
            logger.info(objectMapper.writeValueAsString(message));
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
    }
}
