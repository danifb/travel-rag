package com.danifb.travel_rag.model;

import java.util.UUID;

public class Document {
    private UUID id;
    private String name;
    private String content;

    public Document(UUID id, String name, String content) {
        this.id = id;
        this.name = name;
        this.content = content;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getContent() { return content; }
}