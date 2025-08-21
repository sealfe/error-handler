package com.bipocloud.spell.errorhandler.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class CallbackController {
    private final ElasticsearchMessageCallback callback;

    public CallbackController(ElasticsearchMessageCallback callback) {
        this.callback = callback;
    }

    @PostMapping("/webhook")
    public void receive(@RequestBody ElasticsearchMessage message) {
        log.info(message.getContent());
        callback.handle(message);
    }
}
