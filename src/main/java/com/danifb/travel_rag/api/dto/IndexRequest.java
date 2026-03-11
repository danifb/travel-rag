package com.danifb.travel_rag.api.dto;

public class IndexRequest {
    private String name;
    private String content;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}