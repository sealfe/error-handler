package com.bipocloud.spell.errorhandler.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TApi {


    @GetMapping("ping")
    public String ping() {
        return "pong";
    }

}
