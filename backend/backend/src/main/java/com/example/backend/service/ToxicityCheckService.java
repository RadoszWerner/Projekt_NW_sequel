package com.example.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@Service
public class ToxicityCheckService {

    private final RestTemplate restTemplate;

    @Value("${toxicity.api.url}")
    private String apiUrl;

    @Value("${toxicity.api.key}")
    private String apiKey;

    public ToxicityCheckService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean isToxic(String content) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = "{\"comment\": \"" + content + "\"}";

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, request, Map.class);

            Map<String, Object> responseBody = response.getBody();
            if (!responseBody.containsKey("toxicity_score")) {
                throw new RuntimeException("Unexpected response format: " + responseBody);
            }

            List<Integer> toxicityScores = (List<Integer>) responseBody.get("toxicity_score");
            System.out.println(toxicityScores);
            for (int score : toxicityScores) {
                if (score > 0) {
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            throw new RuntimeException("Failed to check toxicity: " + e.getMessage());
        }
    }
}
