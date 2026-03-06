package com.danifb.travel_rag.model;

import java.util.List;
import java.util.UUID;

public class Chunk {
    private UUID id;
    private UUID documentId;
    private String content;
    private int chunkIndex;
    private List<Double> embedding;

    public Chunk(UUID id, UUID documentId, String content, int chunkIndex) {
        this.id = id;
        this.documentId = documentId;
        this.content = content;
        this.chunkIndex = chunkIndex;
    }

    public UUID getId() { return id; }
    public UUID getDocumentId() { return documentId; }
    public String getContent() { return content; }
    public int getChunkIndex() { return chunkIndex; }
    public List<Double> getEmbedding() { return embedding; }
    public void setEmbedding(List<Double> embedding) { this.embedding = embedding; }
}