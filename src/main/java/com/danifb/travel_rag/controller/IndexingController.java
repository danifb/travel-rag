package com.danifb.travel_rag.controller;

import com.danifb.travel_rag.service.IndexingService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class IndexingController {

    private final IndexingService indexingService;

    public IndexingController(IndexingService indexingService) {
        this.indexingService = indexingService;
    }

    @PostMapping("/index")
    public String indexDocument(@RequestBody IndexRequest request) throws Exception {
        var docId = indexingService.indexDocument(request.name(), request.content());
        return "Document correctly indexed. ID: " + docId;
    }

    public record IndexRequest(String name, String content) {}
}