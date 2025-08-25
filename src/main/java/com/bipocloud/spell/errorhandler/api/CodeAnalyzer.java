package com.bipocloud.spell.errorhandler.api;

import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class CodeAnalyzer {
    private final ChatClient client;
    private final String prompt;

    public CodeAnalyzer(ChatClient client) {
        this.client = client;
        this.prompt = readPrompt();
    }

    private String readPrompt() {
        try {
            return new String(new ClassPathResource("analysis-prompt.txt").getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "";
        }
    }

    public String analyze(CodeRecord record) {
        String codes = record.getStack().stream().map(CodeFrame::getCode).flatMap(n -> n.stream()).collect(Collectors.joining());
        String value = prompt.replace("{{exception_message}}", record.getType()).replace("{{code_snippets}}", codes).replace("{{stack_trace}}", record.getTrace().stream().collect(Collectors.joining()));
        UserMessage message = new UserMessage(value);
        Prompt p = new Prompt(message);
        return client.call(p).getResult().getOutput().getContent();
    }
}
