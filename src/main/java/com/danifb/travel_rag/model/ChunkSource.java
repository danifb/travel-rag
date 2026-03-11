package com.danifb.travel_rag.model;

import java.util.UUID;

public class ChunkSource {
    private UUID documentId;
    private String documentName;
    private int chunkIndex;
    private String content;
    private double score;

    public ChunkSource(UUID documentId, String documentName, int chunkIndex, String content, double score) {
        this.documentId = documentId;
        this.documentName = documentName;
        this.chunkIndex = chunkIndex;
        this.content = content;
        this.score = score;
    }

    public UUID getDocumentId() { return documentId; }
    public String getDocumentName() { return documentName; }
    public int getChunkIndex() { return chunkIndex; }
    public String getContent() { return content; }
    public double getScore() { return score; }
}