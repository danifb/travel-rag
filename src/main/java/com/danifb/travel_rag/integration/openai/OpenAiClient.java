package com.danifb.travel_rag.integration.openai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Component
public class OpenAiClient {

    private static final Logger log = LoggerFactory.getLogger(OpenAiClient.class);

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public OpenAiClient(OpenAiProperties props, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;

        this.restClient = RestClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + props.getApiKey())
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public List<Double> embed(String text) {
        try {
            String body = objectMapper.writeValueAsString(
                    new EmbeddingsRequest("text-embedding-3-small", text)
            );

            String response = restClient.post()
                    .uri("/embeddings")
                    .body(body)
                    .retrieve()
                    .body(String.class);

            return parseFirstEmbedding(response);
        } catch (Exception e) {
            throw new RuntimeException("Failed to call embeddings API", e);
        }
    }

    public String generate(String prompt) {
        try {
            String body = objectMapper.writeValueAsString(
                    new ChatRequest(
                            "gpt-4o-mini",
                            List.of(new Message("user", prompt))
                    )
            );

            String response = restClient.post()
                    .uri("/chat/completions")
                    .body(body)
                    .retrieve()
                    .body(String.class);

            return extractResponseText(response);
        } catch (Exception e) {
            throw new RuntimeException("Failed to call chat completions API", e);
        }
    }

    private List<Double> parseFirstEmbedding(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode embeddingNode = root.path("data").get(0).path("embedding");

            List<Double> embedding = new ArrayList<>();
            for (JsonNode value : embeddingNode) {
                embedding.add(value.asDouble());
            }
            return embedding;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse embedding response", e);
        }
    }

    private String extractResponseText(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            return root.path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();
        } catch (Exception e) {
            log.warn("Failed to parse chat response. Returning raw JSON.", e);
            return json;
        }
    }

    private record EmbeddingsRequest(String model, String input) {}

    private record ChatRequest(String model, List<Message> messages) {}

    private record Message(String role, String content) {}
}