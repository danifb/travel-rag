package com.danifb.travel_rag.api.dto;

import java.util.UUID;

public class IndexResponse {
    private String status;
    private UUID documentId;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public UUID getDocumentId() { return documentId; }
    public void setDocumentId(UUID documentId) { this.documentId = documentId; }
}
