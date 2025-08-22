package com.bipocloud.spell.errorhandler.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ElasticsearchMessage {
    private String title;
    private String content;

    public String extractStackTrace() {
        int idx = content.indexOf("stack_trace:");
        if (idx < 0) {
            return "";
        }
        int start = idx + "stack_trace:".length();
        return content.substring(start).trim();
    }

    public String extractAppName() {
        int idx = title.lastIndexOf(']');
        String name = idx >= 0 ? title.substring(idx + 1) : title;
        return name.endsWith("-copy") ? name.substring(0, name.length() - 5) : name;
    }
}
