package com.danifb.travel_rag.api.controller;

import com.danifb.travel_rag.api.dto.AskRequest;
import com.danifb.travel_rag.api.dto.AskResponse;
import com.danifb.travel_rag.model.ChunkSource;
import com.danifb.travel_rag.service.RagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rag")
public class RagController {

    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    @PostMapping("/ask")
    public ResponseEntity<AskResponse> ask(@RequestBody AskRequest request) {
        RagService.AskResult result = ragService.ask(request.getDocumentIds(), request.getQuestion());

        AskResponse resp = new AskResponse();
        resp.setAnswer(result.getAnswer());
        resp.setSources(result.getSources());

        return ResponseEntity.ok(resp);
    }
}