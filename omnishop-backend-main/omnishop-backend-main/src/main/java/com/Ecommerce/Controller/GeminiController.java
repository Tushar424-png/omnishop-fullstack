package com.Ecommerce.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Ecommerce.dto.GeminiRequest;
import com.Ecommerce.dto.GeminiResponse;
import com.Ecommerce.Service.ChatbotService;

@RestController
@RequestMapping("/api/gemini")
public class GeminiController {

    private final ChatbotService chatbotService;

    public GeminiController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/ask")
    public ResponseEntity<GeminiResponse> ask(@RequestBody GeminiRequest request) {

        if (request.getPrompt() == null || request.getPrompt().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new GeminiResponse("Prompt cannot be empty"));
        }

        String reply = chatbotService.handleUserQuery(request.getPrompt());
        return ResponseEntity.ok(new GeminiResponse(reply));
    }
}
