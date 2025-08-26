package com.bipocloud.spell.errorhandler.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
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
    private final String project;
    private final String webhook;

    public JiraClient(ObjectMapper mapper, @Value("${jira.url}") String url, @Value("${jira.user}") String user, @Value("${jira.token}") String token, @Value("${jira.project}") String project, @Value("${wechat.webhook}") String webhook) {
        this.mapper = mapper;
        this.client = HttpClient.newHttpClient();
        this.url = url;
        this.user = user;
        this.token = token;
        this.project = project;
        this.webhook = webhook;
    }

    public String create(String summary, String description, String email, String author) throws IOException, InterruptedException {
        String id = accountId(email);
        Map<String, Object> fields = new HashMap<>();
        fields.put("project", Map.of("id", project));
        fields.put("summary", summary);
        fields.put("description", description);
        fields.put("issuetype", Map.of("id", "10004"));
        if (!id.isEmpty()) {
            fields.put("assignee", Map.of("id", id));
        }
        Map<String, Object> body = new HashMap<>();
        body.put("fields", fields);
        String auth = Base64.getEncoder().encodeToString((user + ":" + token).getBytes(StandardCharsets.UTF_8));
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url + "/rest/api/2/issue")).header("Authorization", "Basic " + auth).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body))).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Map<String, Object> result = mapper.readValue(response.body(), new TypeReference<Map<String, Object>>() {
        });
        String key = result.getOrDefault("key", "").toString();
        notifyWeChat(email, author, key, summary);
        return key;
    }

    private String accountId(String email) throws IOException, InterruptedException {
        String id = queryAccountId(email);
        if (id.isEmpty() && !"sky.wang@biposervice.com".equals(email)) {
            id = queryAccountId("sky.wang@biposervice.com");
        }
        return id;
    }

    private String queryAccountId(String email) throws IOException, InterruptedException {
        String auth = Base64.getEncoder().encodeToString((user + ":" + token).getBytes(StandardCharsets.UTF_8));
        String query = URLEncoder.encode(email, StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url + "/rest/api/3/user/search?query=" + query)).header("Authorization", "Basic " + auth).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Map<String, Object>> users = mapper.readValue(response.body(), new TypeReference<List<Map<String, Object>>>() {
        });
        if (!users.isEmpty() && users.get(0).get("accountId") != null) {
            return users.get(0).get("accountId").toString();
        }
        return "";
    }

    private void notifyWeChat(String email, String author, String key, String summary) throws IOException, InterruptedException {
        String id = WeChatUsers.userId(email);
        Map<String, Object> markdown = new HashMap<>();
        markdown.put("content", "author-mail: " + email + "\nauthor: " + author + "\nstory: " + key + "\ntitle: " + summary);
        if (!id.isEmpty()) {
            markdown.put("mentioned_list", List.of(id));
        }
        Map<String, Object> body = Map.of("msgtype", "markdown", "markdown", markdown);
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(webhook)).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body))).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public void comment(String key, String text) throws IOException, InterruptedException {
        String auth = Base64.getEncoder().encodeToString((user + ":" + token).getBytes(StandardCharsets.UTF_8));
        Map<String, Object> body = Map.of("body", text);
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url + "/rest/api/2/issue/" + key + "/comment")).header("Authorization", "Basic " + auth).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body))).build();
        client.send(request, HttpResponse.BodyHandlers.discarding());
    }
}
