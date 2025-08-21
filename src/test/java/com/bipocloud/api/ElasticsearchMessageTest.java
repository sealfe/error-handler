package com.bipocloud.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ElasticsearchMessageTest {
    @Test
    void readsMessage() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream stream = getClass().getClassLoader().getResourceAsStream("elasticsearch-message.json");
        ElasticsearchMessage message = mapper.readValue(stream, ElasticsearchMessage.class);
        assertNotNull(message);
        assertEquals("wrapped exception", message.getSource().getMessage());
        assertEquals("com.bipocloud.dukang.serviceonline.hrms.acl.HRMSWrapper", message.getSource().getLoggerName());
    }
}
