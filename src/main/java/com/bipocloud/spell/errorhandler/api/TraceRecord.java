package com.bipocloud.spell.errorhandler.api;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("trace_records")
public class TraceRecord {
    @Id
    private String id;
    private String jiraKey;

    public TraceRecord() {}

    public TraceRecord(String id, String jiraKey) {
        this.id = id;
        this.jiraKey = jiraKey;
    }

    public String getId() {
        return id;
    }

    public String getJiraKey() {
        return jiraKey;
    }
}
