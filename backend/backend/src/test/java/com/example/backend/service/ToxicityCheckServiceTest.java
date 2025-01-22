package com.example.backend.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ToxicityCheckServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ToxicityCheckService toxicityCheckService;

    public ToxicityCheckServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

//    @Test
//    void testIsToxic_WhenToxic_ReturnsTrue() {
//        Map<String, Object> mockResponse = new HashMap<>();
//        mockResponse.put("toxicity_score", List.of(0, 1, 0, 0, 0, 0));
//        ResponseEntity<Map> responseEntity = ResponseEntity.ok(mockResponse);
//
//        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(Map.class)))
//                .thenReturn(responseEntity);
//
//        boolean result = toxicityCheckService.isToxic("i hate you");
//
//        assertTrue(result);
//    }
//
//    @Test
//    void testIsToxic_WhenNotToxic_ReturnsFalse() {
//        // Mock response
//        Map<String, Object> mockResponse = new HashMap<>();
//        mockResponse.put("toxicity_score", List.of(0, 0, 0, 0, 0, 0));
//        ResponseEntity<Map> responseEntity = ResponseEntity.ok(mockResponse);
//
//        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(Map.class)))
//                .thenReturn(responseEntity);
//
//        boolean result = toxicityCheckService.isToxic("hello");
//
//        assertFalse(result);
//    }

    @Test
    void testIsToxic_WhenApiFails_ThrowsException() {
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(Map.class)))
                .thenThrow(new RuntimeException("API error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> toxicityCheckService.isToxic("test"));
        assertTrue(exception.getMessage().contains("Failed to check toxicity"));
    }
}