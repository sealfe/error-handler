package com.bipocloud.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CallbackController {
    private final ElasticsearchMessageCallback callback;

    public CallbackController(ElasticsearchMessageCallback callback) {
        this.callback = callback;
    }

    @PostMapping("/callback")
    public void receive(@RequestBody ElasticsearchMessage message) {
        callback.handle(message);
    }
}
