package errorhandler;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class JiraIssueCreator {
    public void createIssue(JiraIssue issue) throws IOException, InterruptedException, ReflectiveOperationException {
        setEnv("JIRA_TOKEN", issue.token);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(issue.url + "/rest/api/2/issue"))
            .header("Authorization", "Bearer " + System.getenv("JIRA_TOKEN"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(issue.toJson()))
            .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private void setEnv(String key, String value) throws ReflectiveOperationException {
        Map<String, String> env = System.getenv();
        Class<?> c = env.getClass();
        Field f = c.getDeclaredField("m");
        f.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, String> m = (Map<String, String>) f.get(env);
        m.put(key, value);
    }
}

class JiraIssue {
    String url;
    String token;
    String projectKey;
    String summary;
    String description;
    String issueType;

    String toJson() {
        return "{" +
            "\"fields\":{" +
            "\"project\":{" +
            "\"key\":\"" + projectKey + "\"}," +
            "\"summary\":\"" + summary + "\"," +
            "\"description\":\"" + description + "\"," +
            "\"issuetype\":{" +
            "\"name\":\"" + issueType + "\"}}" +
            "}";
    }
}
