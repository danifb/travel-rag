package com.danifb.travel_rag.api.controller;

import com.danifb.travel_rag.api.dto.IndexRequest;
import com.danifb.travel_rag.api.dto.IndexResponse;
import com.danifb.travel_rag.model.Document;
import com.danifb.travel_rag.service.IndexingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class IndexController {

    private final IndexingService indexingService;

    public IndexController(IndexingService indexingService) {
        this.indexingService = indexingService;
    }

    @PostMapping("/index")
    public ResponseEntity<IndexResponse> index(@RequestBody IndexRequest request) {
        Document doc = new Document(UUID.randomUUID(), request.getName(), request.getContent());
        UUID id = indexingService.index(doc);

        IndexResponse resp = new IndexResponse();
        resp.setStatus("ok");
        resp.setDocumentId(id);
        return ResponseEntity.ok(resp);
    }

}