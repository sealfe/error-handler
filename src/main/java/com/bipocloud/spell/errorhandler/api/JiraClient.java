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

    public JiraClient(ObjectMapper mapper, @Value("${jira.url}") String url, @Value("${jira.user}") String user, @Value("${jira.token}") String token, @Value("${jira.project}") String project) {
        this.mapper = mapper;
        this.client = HttpClient.newHttpClient();
        this.url = url;
        this.user = user;
        this.token = token;
        this.project = project;
    }

    public void create(String summary, String description, String assignee) throws IOException, InterruptedException {
        String id = accountId(assignee);
        String bug = bugId();
        Map<String, Object> fields = new HashMap<>();
        fields.put("project", Map.of("id", project));
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
        client.send(request, HttpResponse.BodyHandlers.ofString());
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

    private String bugId() throws IOException, InterruptedException {
        String auth = Base64.getEncoder().encodeToString((user + ":" + token).getBytes(StandardCharsets.UTF_8));
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url + "/rest/api/3/issuetype")).header("Authorization", "Basic " + auth).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Map<String, Object>> types = mapper.readValue(response.body(), new TypeReference<List<Map<String, Object>>>() {});
        for (Map<String, Object> type : types) {
            if ("Bug".equals(type.get("name")) && type.get("id") != null) {
                return type.get("id").toString();
            }
        }
        return "";
    }
}
