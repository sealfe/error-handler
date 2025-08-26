package com.bipocloud.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.bipocloud.spell.errorhandler.api.WeChatUsers;
import org.junit.jupiter.api.Test;

class WeChatUsersTest {
    @Test
    void mapsEmailToId() {
        assertEquals("13564037236", WeChatUsers.userId("sky.wang@biposervice.com"));
        assertEquals("", WeChatUsers.userId("none@biposervice.com"));
    }
}
