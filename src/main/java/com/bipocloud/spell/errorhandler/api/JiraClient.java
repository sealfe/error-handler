package com.bipocloud.spell.errorhandler.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JiraClient {
    private final ObjectMapper mapper;
    private final HttpClient client;
    private final String url;
    private final String user;
    private final String token;

    public JiraClient(ObjectMapper mapper, @Value("${jira.url}") String url, @Value("${jira.user}") String user, @Value("${jira.token}") String token) {
        this.mapper = mapper;
        this.client = HttpClient.newHttpClient();
        this.url = url;
        this.user = user;
        this.token = token;
    }

    public void create(Map<String, Object> body) throws IOException, InterruptedException {
        String auth = Base64.getEncoder().encodeToString((user + ":" + token).getBytes(StandardCharsets.UTF_8));
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url + "/rest/api/2/issue")).header("Authorization", "Basic " + auth).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body))).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
