package com.danifb.travel_rag.service;

import com.danifb.travel_rag.model.Chunk;
import com.danifb.travel_rag.model.Document;
import com.danifb.travel_rag.repo.ChunkRepository;
import com.danifb.travel_rag.integration.openai.OpenAiClient;
import com.danifb.travel_rag.repo.util.VectorUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class IndexingService {

    private final ChunkingService chunkingService;
    private final OpenAiClient openAiClient;
    private final ChunkRepository chunkRepository;

    public IndexingService(ChunkingService chunkingService,
                           OpenAiClient openAiClient,
                           ChunkRepository chunkRepository) {
        this.chunkingService = chunkingService;
        this.openAiClient = openAiClient;
        this.chunkRepository = chunkRepository;
    }

    public UUID index(Document document) {
        chunkRepository.ensureDocument(document.getId(), document.getName());

        List<String> pieces = chunkingService.splitIntoChunks(document.getContent());

        for (int i = 0; i < pieces.size(); i++) {
            Chunk chunk = new Chunk(
                    UUID.randomUUID(),
                    document.getId(),
                    pieces.get(i),
                    i
            );

            List<Double> embedding = openAiClient.embed(chunk.getContent());
            chunk.setEmbedding(embedding);

            String embeddingLiteral = VectorUtils.toVectorLiteral(embedding);
            chunkRepository.saveChunk(chunk, embeddingLiteral); // uses the overload above
        }

        return document.getId();
    }
}