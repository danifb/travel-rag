package com.danifb.travel_rag.controller;

import com.danifb.travel_rag.service.RagService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class SearchController {

    private final RagService ragService;

    public SearchController(RagService ragService) {
        this.ragService = ragService;
    }

    @PostMapping("/search")
    public SearchResponse search(@RequestBody SearchRequest request) throws Exception {
        String answer = ragService.answer(request.question());
        return new SearchResponse(request.question(), answer);
    }

    public record SearchRequest(String question) {}
    public record SearchResponse(String question, String answer) {}
}