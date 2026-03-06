package com.danifb.travel_rag.service;

import com.danifb.travel_rag.model.Chunk;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ChunkingService {

    private static final int CHUNK_SIZE = 500;
    private static final int OVERLAP = 50;

    public List<Chunk> chunkDocument(UUID documentId, String content) {
        List<Chunk> chunks = new ArrayList<>();

        String[] paragraphs = content.split("\n\n");

        StringBuilder currentChunk = new StringBuilder();
        int chunkIndex = 0;

        for (String paragraph : paragraphs) {
            paragraph = paragraph.trim();
            if (paragraph.isEmpty()) continue;

            if (currentChunk.length() + paragraph.length() > CHUNK_SIZE
                    && !currentChunk.isEmpty()) {

                chunks.add(new Chunk(
                        UUID.randomUUID(),
                        documentId,
                        currentChunk.toString().trim(),
                        chunkIndex++
                ));

                String previousText = currentChunk.toString();
                int overlapStart = Math.max(0, previousText.length() - OVERLAP);
                currentChunk = new StringBuilder(previousText.substring(overlapStart));
            }

            currentChunk.append(paragraph).append("\n\n");
        }

        if (!currentChunk.isEmpty()) {
            chunks.add(new Chunk(
                    UUID.randomUUID(),
                    documentId,
                    currentChunk.toString().trim(),
                    chunkIndex
            ));
        }

        return chunks;
    }
}