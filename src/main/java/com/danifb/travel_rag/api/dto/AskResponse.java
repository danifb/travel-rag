package com.danifb.travel_rag.api.dto;

import com.danifb.travel_rag.model.ChunkSource;

import java.util.List;

public class AskResponse {
    private String answer;
    private List<ChunkSource> sources;

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public List<ChunkSource> getSources() { return sources; }
    public void setSources(List<ChunkSource> sources) { this.sources = sources; }
}