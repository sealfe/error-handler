package com.bipocloud.spell.errorhandler.api;

public interface ElasticsearchMessageCallback {
    void handle(ElasticsearchMessage message);
}
