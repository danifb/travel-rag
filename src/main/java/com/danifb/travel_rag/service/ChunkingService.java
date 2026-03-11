package com.danifb.travel_rag.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChunkingService {

    private static final int DEFAULT_CHUNK_SIZE = 1000;
    private static final int DEFAULT_OVERLAP = 200;

    public List<String> splitIntoChunks(String content) {
        return splitIntoChunks(content, DEFAULT_CHUNK_SIZE, DEFAULT_OVERLAP);
    }

    public List<String> splitIntoChunks(String content, int maxChunkSize, int overlap) {
        if (content == null || content.isBlank()) {
            return List.of();
        }
        if (maxChunkSize <= 0) {
            throw new IllegalArgumentException("maxChunkSize must be > 0");
        }
        if (overlap < 0) {
            throw new IllegalArgumentException("overlap must be >= 0");
        }
        // Critical: overlap must be smaller than chunk size, otherwise you can loop forever
        if (overlap >= maxChunkSize) {
            throw new IllegalArgumentException("overlap must be < maxChunkSize");
        }

        List<String> chunks = new ArrayList<>();

        int contentLength = content.length();
        int start = 0;

        while (start < contentLength) {
            int end = Math.min(start + maxChunkSize, contentLength);

            String chunk = content.substring(start, end).trim();
            if (!chunk.isEmpty()) {
                chunks.add(chunk);
            }

            // If we've reached the end, stop (prevents repeating the last chunk)
            if (end == contentLength) {
                break;
            }

            int nextStart = end - overlap;
            // Extra safety: ensure forward progress
            if (nextStart <= start) {
                nextStart = end;
            }

            start = nextStart;
        }

        return chunks;
    }
}