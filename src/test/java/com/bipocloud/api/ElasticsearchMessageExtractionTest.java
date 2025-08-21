package com.bipocloud.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import com.bipocloud.spell.errorhandler.api.ElasticsearchMessage;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ElasticsearchMessageExtractionTest {
    @Test
    void extractsFields() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream stream = getClass().getClassLoader().getResourceAsStream("notification-message.json");
        ElasticsearchMessage message = mapper.readValue(stream, ElasticsearchMessage.class);
        assertNotNull(message);
        String stack = message.extractStackTrace();
        String app = message.extractAppName();
        assertEquals("java.io.IOException: Broken pipe\n\tat java.base/sun.nio.ch.FileDispatcherImpl.write0(Native Method)", stack);
        assertEquals("dukang-service-online", app);
    }
}
