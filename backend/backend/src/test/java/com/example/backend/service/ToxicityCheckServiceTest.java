package com.example.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ToxicityCheckServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ToxicityCheckService toxicityCheckService;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(toxicityCheckService, "apiUrl", "http://fake-api.com");
        ReflectionTestUtils.setField(toxicityCheckService, "apiKey", "test-key");
    }

    @Test
    void isToxic_ShouldReturnTrue_WhenAnyScoreIsPositive() {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("toxicity_score", List.of(0, 1, 0, 0, 0, 0));

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(responseEntity);

        boolean result = toxicityCheckService.isToxic("Some toxic content");

        assertTrue(result);
    }

    @Test
    void isToxic_ShouldReturnFalse_WhenAllScoresAreZero() {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("toxicity_score", List.of(0, 0, 0, 0, 0, 0));

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(responseEntity);

        boolean result = toxicityCheckService.isToxic("Neutral comment");

        assertFalse(result);
    }

    @Test
    void getToxicityScores_ShouldReturnListOfBooleans() {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("toxicity_score", List.of(0, 1, 0, 1, 0, 1));

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(responseEntity);

        List<Boolean> scores = toxicityCheckService.getToxicityScores("Sample comment");

        assertEquals(List.of(false, true, false, true, false, true), scores);
    }

    @Test
    void getToxicityScores_ShouldThrow_WhenResponseMissingToxicityScoreKey() {
        Map<String, Object> badResponse = new HashMap<>();
        badResponse.put("unexpected", "data");

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(badResponse, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(responseEntity);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> toxicityCheckService.getToxicityScores("test"));

        assertTrue(exception.getMessage().contains("Unexpected response format"));
    }

    @Test
    void testIsToxic_WhenApiFails_ThrowsException() {
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(Map.class)))
                .thenThrow(new RuntimeException("API error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> toxicityCheckService.isToxic("test"));
        assertTrue(exception.getMessage().contains("Failed to check toxicity"));
    }
}

