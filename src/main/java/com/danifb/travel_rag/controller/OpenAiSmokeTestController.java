package com.danifb.travel_rag.controller;

import com.danifb.travel_rag.service.OpenAiClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OpenAiSmokeTestController {

    private final OpenAiClient client;

    public OpenAiSmokeTestController(OpenAiClient client) {
        this.client = client;
    }

    @GetMapping("/test-openai")
    public String test() throws Exception {
        return client.smokeTest();
    }
}