package com.danifb.travel_rag.service;

import com.danifb.travel_rag.repo.ChunkRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RagService {

    private final OpenAiClient openAiClient;
    private final ChunkRepository chunkRepository;

    public RagService(OpenAiClient openAiClient, ChunkRepository chunkRepository) {
        this.openAiClient = openAiClient;
        this.chunkRepository = chunkRepository;
    }

    public String answer(String userQuestion) throws Exception {

        System.out.println("Question: " + userQuestion);

        List<Double> questionEmbedding = openAiClient.embed(userQuestion);
        String questionVector = toVectorLiteral(questionEmbedding);

        List<String> relevantChunks = chunkRepository.findSimilarChunks(questionVector, 3);
        System.out.println("Relevant chunks found: " + relevantChunks.size());

        StringBuilder context = new StringBuilder();
        for (int i = 0; i < relevantChunks.size(); i++) {
            context.append("--- Fragment ").append(i + 1).append(" ---\n");
            context.append(relevantChunks.get(i)).append("\n\n");
        }

        String prompt = """
        You are an assistant specialized in flight search.
        Answer the user's question using ONLY the information
        provided in the following context.
        If the information is not in the context, state it clearly.
        
        CONTEXT:
        %s
        
        USER QUESTION:
        %s
        
        ANSWER:
        """.formatted(context.toString(), userQuestion);

        String response = openAiClient.chat(prompt);
        System.out.println("Response generated successfully");

        return response;
    }

    private String toVectorLiteral(List<Double> vec) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < vec.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(vec.get(i));
        }
        sb.append("]");
        return sb.toString();
    }
}