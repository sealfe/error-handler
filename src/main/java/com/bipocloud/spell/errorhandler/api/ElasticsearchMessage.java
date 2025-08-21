package com.bipocloud.spell.errorhandler.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ElasticsearchMessage {
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String extractStackTrace() {
        if (content == null) {
            return null;
        }
        String marker = "stack_trace:\n";
        int index = content.indexOf(marker);
        if (index == -1) {
            return null;
        }
        return content.substring(index + marker.length()).trim();
    }

    public String extractAppName() {
        if (content == null) {
            return null;
        }
        int end = content.indexOf("最近");
        if (end == -1) {
            return null;
        }
        int start = content.lastIndexOf(']', end);
        if (start == -1 || start + 1 >= end) {
            return null;
        }
        return content.substring(start + 1, end);
    }
}
