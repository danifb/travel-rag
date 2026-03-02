package com.danifb.travel_rag.controller;

import com.danifb.travel_rag.repo.ChunkRepository;
import com.danifb.travel_rag.repo.DocumentRepository;
import com.danifb.travel_rag.service.OpenAiClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class EmbeddingStorageController {

    private final OpenAiClient openAi;
    private final DocumentRepository documents;
    private final ChunkRepository chunks;

    public EmbeddingStorageController(OpenAiClient openAi,
                                      DocumentRepository documents,
                                      ChunkRepository chunks) {
        this.openAi = openAi;
        this.documents = documents;
        this.chunks = chunks;
    }

    @GetMapping("/store-test-embedding")
    public String storeTestEmbedding() throws Exception {
        UUID docId = UUID.randomUUID();
        UUID chunkId = UUID.randomUUID();

        documents.insert(docId, "test-document");

        String text = "This is my first chunk stored in pgvector.";
        List<Double> vec = openAi.embed(text);

        String vectorLiteral = toPgVectorLiteral(vec);
        chunks.insert(chunkId, docId, text, vectorLiteral);

        return "Stored chunk=" + chunkId + " doc=" + docId + " vecSize=" + vec.size();
    }

    private static String toPgVectorLiteral(List<Double> vec) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < vec.size(); i++) {
            if (i > 0) sb.append(',');
            sb.append(vec.get(i));
        }
        sb.append(']');
        return sb.toString();
    }
}