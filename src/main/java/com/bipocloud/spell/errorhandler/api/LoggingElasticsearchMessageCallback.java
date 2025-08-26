package com.bipocloud.spell.errorhandler.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
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
    private final JiraClient jira;
    private final TraceRecordRepository records;

    public LoggingElasticsearchMessageCallback(ObjectMapper objectMapper, CodeRecordBuilder builder, CodeAnalyzer analyzer, JiraClient jira, TraceRecordRepository records) {
        this.objectMapper = objectMapper;
        this.stackTraceLocator = new StackTraceLocator();
        this.builder = builder;
        this.analyzer = analyzer;
        this.jira = jira;
        this.records = records;
    }

    public void handle(ElasticsearchMessage message) {
        try {
            String stack = message.extractStackTrace();
            if (stack != null) {
                String id = md5(stack);
                if (records.existsById(id)) {
                    return;
                }
                StackTraceRootCause cause = stackTraceLocator.findRootCause(stack);
                if (cause != null) {
                    CodeRecord record = builder.build(cause, message.extractAppName(), stack);
                    String result = analyzer.analyze(record);
                    String email = record.getStack().isEmpty() ? "" : record.getStack().get(0).getMail();
                    String author = record.getStack().isEmpty() ? "" : record.getStack().get(0).getAuthor();
                    String key = jira.create(record.getType(), record.description(result), email, author);
                    records.save(new TraceRecord(id, key));
                    return;
                }
            }
            logger.info(objectMapper.writeValueAsString(message));
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
    }

    private String md5(String input) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder builder = new StringBuilder();
        for (byte b : hash) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }
}
