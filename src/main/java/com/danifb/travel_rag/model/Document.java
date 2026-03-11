package com.danifb.travel_rag.model;

import java.util.UUID;

public class Document {
    private final UUID id;
    private final String name;
    private final String content;

    public Document(UUID id, String name, String content) {
        this.id = id;
        this.name = name;
        this.content = content;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getContent() { return content; }
}