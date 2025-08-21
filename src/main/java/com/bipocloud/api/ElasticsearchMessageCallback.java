package com.bipocloud.api;

public interface ElasticsearchMessageCallback {
    void handle(ElasticsearchMessage message);
}
