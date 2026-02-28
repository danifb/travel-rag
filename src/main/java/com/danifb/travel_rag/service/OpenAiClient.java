package com.danifb.travel_rag.service;

import com.danifb.travel_rag.config.OpenAiProperties;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class OpenAiClient {

    private final OpenAiProperties props;
    private final HttpClient http = HttpClient.newHttpClient();

    public OpenAiClient(OpenAiProperties props) {
        this.props = props;
    }

    public String smokeTest() throws Exception {
        if (props.getApiKey() == null || props.getApiKey().isBlank()) {
            return "OPENAI_API_KEY is missing. Set it as an environment variable.";
        }

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
}