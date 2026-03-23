package com.Ecommerce.Service;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;
import java.util.List;
@Service
public class GeminiService {

    private final WebClient webClient;

    @Value("${groq.api.key}")
    private String apiKey;

    private final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";

    public GeminiService(WebClient webClient) {
        this.webClient = webClient;
    }

    public String askGroq(String systemPrompt, String userPrompt) {

        Map<String, Object> requestBody = Map.of(
            "model", "llama-3.1-8b-instant",
            "messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userPrompt)
            ),
            "temperature", 0.3
        );

        try {
            return webClient.post()
                    .uri(GROQ_URL)
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .map(response -> {
                        List choices = (List) response.get("choices");
                        Map firstChoice = (Map) choices.get(0);
                        Map message = (Map) firstChoice.get("message");
                        return (String) message.get("content");
                    })
                    .block();

        } catch (WebClientResponseException e) {
            System.out.println("Groq Error Body: " + e.getResponseBodyAsString());
            return "Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString();
        }
    }
}
