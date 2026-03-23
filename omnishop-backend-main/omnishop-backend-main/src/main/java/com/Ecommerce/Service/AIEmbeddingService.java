package com.Ecommerce.Service;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.*;
import java.util.Map;
import java.util.List;

@Service
public class AIEmbeddingService {

    @Value("${jina.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.create("https://api.jina.ai");

    public float[] getEmbedding(String text) {

        Map<String, Object> body = Map.of(
                "model", "jina-embeddings-v2-base-en",
                "input", text
        );

        Map response = webClient.post()
                .uri("/v1/embeddings")
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        List data = (List) response.get("data");
        Map first = (Map) data.get(0);
        List<Double> values = (List<Double>) first.get("embedding");

        float[] vector = new float[values.size()];

        for (int i = 0; i < values.size(); i++) {
            vector[i] = values.get(i).floatValue();
        }

        return vector;
    }
}
