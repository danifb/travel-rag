package com.danifb.travel_rag.service;

import com.danifb.travel_rag.model.Chunk;
import com.danifb.travel_rag.repo.ChunkRepository;
import com.danifb.travel_rag.repo.DocumentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class IndexingService {

    private final DocumentRepository documentRepository;
    private final ChunkRepository chunkRepository;
    private final ChunkingService chunkingService;
    private final OpenAiClient openAiClient;

    public IndexingService(DocumentRepository documentRepository,
                           ChunkRepository chunkRepository,
                           ChunkingService chunkingService,
                           OpenAiClient openAiClient) {
        this.documentRepository = documentRepository;
        this.chunkRepository = chunkRepository;
        this.chunkingService = chunkingService;
        this.openAiClient = openAiClient;
    }

    public UUID indexDocument(String documentName, String content) throws Exception {

        UUID documentId = UUID.randomUUID();
        documentRepository.insert(documentId, documentName);
        System.out.println("Document saved with ID: " + documentId);

        List<Chunk> chunks = chunkingService.chunkDocument(documentId, content);
        System.out.println("Total chunks created: " + chunks.size());

        for (int i = 0; i < chunks.size(); i++) {
            Chunk chunk = chunks.get(i);
            System.out.println("Processing chunk " + (i + 1) + "/" + chunks.size());

            List<Double> embedding = openAiClient.embed(chunk.getContent());

            String embeddingLiteral = toVectorLiteral(embedding);

            chunkRepository.insert(
                    chunk.getId(),
                    chunk.getDocumentId(),
                    chunk.getContent(),
                    chunk.getChunkIndex(),
                    embeddingLiteral
            );

            if (i < chunks.size() - 1) {
                Thread.sleep(200);
            }
        }

        System.out.println("Indexación completada!");
        return documentId;
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