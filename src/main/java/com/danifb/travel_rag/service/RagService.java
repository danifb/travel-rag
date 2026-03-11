package com.danifb.travel_rag.service;

import com.danifb.travel_rag.model.ChunkSource;
import com.danifb.travel_rag.repo.ChunkRepository;
import com.danifb.travel_rag.integration.openai.OpenAiClient;
import com.danifb.travel_rag.repo.util.VectorUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RagService {

    private final OpenAiClient openAiClient;
    private final ChunkRepository chunkRepository;
    private final PromptBuilder promptBuilder;

    public RagService(OpenAiClient openAiClient,
                      ChunkRepository chunkRepository,
                      PromptBuilder promptBuilder) {
        this.openAiClient = openAiClient;
        this.chunkRepository = chunkRepository;
        this.promptBuilder = promptBuilder;
    }

    public AskResult ask(List<UUID> documentIds, String question) {
        List<Double> qEmbedding = openAiClient.embed(question);
        String qVector = VectorUtils.toVectorLiteral(qEmbedding);

        List<ChunkSource> sources = chunkRepository.findSimilarSourcesForDocuments(documentIds, qVector, 5);

        // Prompt uses only the chunk text
        List<String> chunkTexts = sources.stream().map(ChunkSource::getContent).toList();
        String prompt = promptBuilder.buildFlightRagPrompt(question, chunkTexts);

        String answer = openAiClient.generate(prompt);

        return new AskResult(answer, sources);
    }

    public static class AskResult {
        private String answer;
        private List<ChunkSource> sources;

        public AskResult(String answer, List<ChunkSource> sources) {
            this.answer = answer;
            this.sources = sources;
        }

        public String getAnswer() { return answer; }
        public List<ChunkSource> getSources() { return sources; }
    }
}