package com.danifb.travel_rag.service;

import com.danifb.travel_rag.config.OpenAiProperties;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class OpenAiClient {

    private final OpenAiProperties props;
    private final HttpClient http = HttpClient.newHttpClient();

    public OpenAiClient(OpenAiProperties props) {
        this.props = props;
    }

    private void ensureApiKey() {
        if (props.getApiKey() == null || props.getApiKey().isBlank()) {
            throw new IllegalStateException("OPENAI_API_KEY is missing. Set it as an environment variable.");
        }
    }

    public String smokeTest() throws Exception {
        ensureApiKey();

        String body = """
        {
          "model": "gpt-4o-mini",
          "input": "Reply with: OpenAI smoke test OK"
        }
        """;

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/responses"))
                .header("Authorization", "Bearer " + props.getApiKey())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        return "HTTP " + res.statusCode() + "\n" + res.body();
    }

    public List<Double> embed(String text) throws Exception {
        ensureApiKey();

        // Simple JSON escaping for quotes/backslashes/newlines
        String safe = text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");

        String body = """
        {
          "model": "text-embedding-3-small",
          "input": "%s"
        }
        """.formatted(safe);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/embeddings"))
                .header("Authorization", "Bearer " + props.getApiKey())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());

        if (res.statusCode() / 100 != 2) {
            throw new IllegalStateException("Embeddings call failed. HTTP " + res.statusCode() + "\n" + res.body());
        }

        return parseFirstEmbedding(res.body());
    }

    private static List<Double> parseFirstEmbedding(String json) {
        Pattern p = Pattern.compile("\"embedding\"\\s*:\\s*\\[(.*?)]", Pattern.DOTALL);
        Matcher m = p.matcher(json);
        if (!m.find()) {
            throw new IllegalStateException("Could not find embedding array in response JSON.");
        }

        String inside = m.group(1).trim();
        if (inside.isEmpty()) return List.of();

        String[] parts = inside.split(",");
        List<Double> vec = new ArrayList<>(parts.length);
        for (String part : parts) {
            String s = part.trim();
            if (!s.isEmpty()) vec.add(Double.parseDouble(s));
        }
        return vec;
    }
}