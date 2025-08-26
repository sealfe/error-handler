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
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
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

    public void create(String summary, String description, String assignee) throws IOException, InterruptedException {
        String id = accountId(assignee);
        String projectId = projectId(project);
        String bug = "10004";
        Map<String, Object> fields = new HashMap<>();
        fields.put("project", Map.of("id", projectId));
        fields.put("summary", summary);
        fields.put("description", description);
        if (!bug.isEmpty()) {
            fields.put("issuetype", Map.of("id", bug));
        } else {
            fields.put("issuetype", Map.of("name", "Bug"));
        }
        if (!id.isEmpty()) {
            fields.put("assignee", Map.of("id", id));
        }
        String started = OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
        Map<String, Object> update = Map.of("worklog", List.of(Map.of("add", Map.of("started", started, "timeSpent", "60m"))));
        Map<String, Object> body = new HashMap<>();
        body.put("fields", fields);
        body.put("update", update);
        String auth = Base64.getEncoder().encodeToString((user + ":" + token).getBytes(StandardCharsets.UTF_8));
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url + "/rest/api/2/issue")).header("Authorization", "Basic " + auth).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body))).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Map<String, Object> result = mapper.readValue(response.body(), new TypeReference<Map<String, Object>>() {});
        String key = result.getOrDefault("key", "").toString();
        notifyWeChat(assignee, key, summary);
    }

    private String accountId(String email) throws IOException, InterruptedException {
        String auth = Base64.getEncoder().encodeToString((user + ":" + token).getBytes(StandardCharsets.UTF_8));
        String query = URLEncoder.encode(email, StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url + "/rest/api/3/user/search?query=" + query)).header("Authorization", "Basic " + auth).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Map<String, Object>> users = mapper.readValue(response.body(), new TypeReference<List<Map<String, Object>>>() {});
        if (!users.isEmpty() && users.get(0).get("accountId") != null) {
            return users.get(0).get("accountId").toString();
        }
        return "";
    }

    private String projectId(String name) throws IOException, InterruptedException {
        String auth = Base64.getEncoder().encodeToString((user + ":" + token).getBytes(StandardCharsets.UTF_8));
        String query = URLEncoder.encode(name, StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url + "/rest/api/3/project/search?query=" + query)).header("Authorization", "Basic " + auth).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Map<String, Object> result = mapper.readValue(response.body(), new TypeReference<Map<String, Object>>() {});
        List<Map<String, Object>> values = (List<Map<String, Object>>) result.get("values");
        if (values != null && !values.isEmpty() && values.get(0).get("id") != null) {
            return values.get(0).get("id").toString();
        }
        return "";
    }

    private void notifyWeChat(String assignee, String key, String summary) throws IOException, InterruptedException {
        Map<String, Object> markdown = Map.of("content", "assignee: " + assignee + "\nstory: " + key + "\ntitle: " + summary);
        Map<String, Object> body = Map.of("msgtype", "markdown", "markdown", markdown);
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(webhook)).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body))).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

}
