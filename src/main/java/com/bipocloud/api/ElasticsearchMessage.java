package com.bipocloud.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ElasticsearchMessage {
    @JsonProperty("_index")
    private String index;
    @JsonProperty("_type")
    private String type;
    @JsonProperty("_id")
    private String id;
    @JsonProperty("_version")
    private int version;
    @JsonProperty("_score")
    private int score;
    @JsonProperty("_ignored")
    private List<String> ignored;
    @JsonProperty("_source")
    private Source source;
    private Map<String, List<Object>> fields;
    @JsonProperty("ignored_field_values")
    private Map<String, List<Object>> ignoredFieldValues;

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public List<String> getIgnored() {
        return ignored;
    }

    public void setIgnored(List<String> ignored) {
        this.ignored = ignored;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Map<String, List<Object>> getFields() {
        return fields;
    }

    public void setFields(Map<String, List<Object>> fields) {
        this.fields = fields;
    }

    public Map<String, List<Object>> getIgnoredFieldValues() {
        return ignoredFieldValues;
    }

    public void setIgnoredFieldValues(Map<String, List<Object>> ignoredFieldValues) {
        this.ignoredFieldValues = ignoredFieldValues;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Source {
        private String message;
        @JsonProperty("@version")
        private int version;
        @JsonProperty("thread_name")
        private String threadName;
        private String level;
        @JsonProperty("instance_name")
        private String instanceName;
        private String host;
        @JsonProperty("stack_trace")
        private String stackTrace;
        @JsonProperty("APP_NAME")
        private String appName;
        @JsonProperty("app_name")
        private String appNameLower;
        private String type;
        @JsonProperty("logger_name")
        private String loggerName;
        private int port;
        @JsonProperty("@timestamp")
        private String timestamp;
        @JsonProperty("level_value")
        private int levelValue;
        @JsonProperty("app_port")
        private String appPort;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public String getThreadName() {
            return threadName;
        }

        public void setThreadName(String threadName) {
            this.threadName = threadName;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getInstanceName() {
            return instanceName;
        }

        public void setInstanceName(String instanceName) {
            this.instanceName = instanceName;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getStackTrace() {
            return stackTrace;
        }

        public void setStackTrace(String stackTrace) {
            this.stackTrace = stackTrace;
        }

        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }

        public String getAppNameLower() {
            return appNameLower;
        }

        public void setAppNameLower(String appNameLower) {
            this.appNameLower = appNameLower;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getLoggerName() {
            return loggerName;
        }

        public void setLoggerName(String loggerName) {
            this.loggerName = loggerName;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public int getLevelValue() {
            return levelValue;
        }

        public void setLevelValue(int levelValue) {
            this.levelValue = levelValue;
        }

        public String getAppPort() {
            return appPort;
        }

        public void setAppPort(String appPort) {
            this.appPort = appPort;
        }
    }
}
