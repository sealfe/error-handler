package com.bipocloud.spell.errorhandler.api;

import ch.qos.logback.core.util.StringUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class CodeAnalyzer {
    private final ObjectMapper mapper;
    private final HttpClient client;
    private final String url;
    private final String key;
    private final String prompt;

    public CodeAnalyzer(ObjectMapper mapper, @Value("${spring.ai.openai.base-url}") String base, @Value("${spring.ai.openai.api-key}") String key) {
        this.mapper = mapper;
        this.client = HttpClient.newHttpClient();
        this.url = base + "/chat/completions";
        this.key = key;
        this.prompt = readPrompt();
    }

    private String readPrompt() {
        try {
            return new String(new ClassPathResource("analysis-prompt.txt").getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "";
        }
    }

    public String analyze(CodeRecord record) throws IOException, InterruptedException {
        Map<String, Object> body = new HashMap<>();
        body.put("model", "Qwen/Qwen3-Coder-30B-A3B-Instruct");
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        String codes = record.getStack().stream().map(n -> n.getCode()).flatMap(n -> n.stream()).collect(Collectors.joining());
        String value = prompt.replace("{{exception_message}}", record.getType()).replace("{{code_snippets}}", codes).replace("{{stack_trace}}", record.getTrace().stream().collect(Collectors.joining()));
        message.put("content", value);
        body.put("messages", List.of(message));
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header("Authorization", "Bearer " + key).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body))).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Map<?, ?> json = mapper.readValue(response.body(), Map.class);
        List<?> choices = (List<?>) json.get("choices");
        if (choices != null && !choices.isEmpty()) {
            Map<?, ?> first = (Map<?, ?>) choices.get(0);
            Map<?, ?> msg = (Map<?, ?>) first.get("message");
            if (msg != null && msg.get("content") != null) {
                return msg.get("content").toString();
            }
        }
        return "";
    }
}
