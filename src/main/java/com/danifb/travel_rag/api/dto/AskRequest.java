package com.danifb.travel_rag.api.dto;

import java.util.List;
import java.util.UUID;

public class AskRequest {
    private List<UUID> documentIds; // optional
    private String question;

    public List<UUID> getDocumentIds() { return documentIds; }
    public void setDocumentIds(List<UUID> documentIds) { this.documentIds = documentIds; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
}