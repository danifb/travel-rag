package com.danifb.travel_rag.service;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PromptBuilder {

    public String buildFlightRagPrompt(String question, List<String> chunks) {
        String context = String.join("\n\n", chunks);

        return """
                You are a helpful flight assistant.

                Use the context below to answer the question.
                If the answer is not in the context, say you don't know.

                Context:
                %s

                Question:
                %s

                Answer:
                """.formatted(context, question);
    }
}