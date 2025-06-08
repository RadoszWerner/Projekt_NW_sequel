package com.example.backend.controller;

import com.example.backend.dto.DeletedPostDTO;
import com.example.backend.filter.JWTAuthenticationFilter;
import com.example.backend.service.DeletedPostService;
import com.example.backend.service.JWTService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DeletedPostController.class)
@AutoConfigureMockMvc(addFilters = false)
class DeletedPostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JWTService jwtService;

    @MockitoBean
    private DeletedPostService deletedPostService;

    @MockitoBean
    private JWTAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getDeletedPosts_ShouldReturnListOfDeletedPostDTOs() throws Exception {
        DeletedPostDTO dto = new DeletedPostDTO();
        dto.setId(1L);
        dto.setReason("Toxic content");

        when(deletedPostService.getAllDeletedPosts()).thenReturn(List.of(dto));

        mockMvc.perform(get("/deletedposts/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].reason").value("Toxic content"));
    }

    @Test
    void restoreComment_ShouldReturnSuccessMessage_WhenValid() throws Exception {
        Map<String, Long> requestBody = Map.of("postId", 1L, "userId", 2L);

        doNothing().when(deletedPostService).restorePost(1L, 2L);

        mockMvc.perform(put("/deletedposts/restore")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(content().string("Post przywrócony pomyślnie"));
    }

    @Test
    void restoreComment_ShouldReturnErrorMessage_WhenExceptionThrown() throws Exception {
        Map<String, Long> requestBody = Map.of("postId", 1L, "userId", 2L);

        doThrow(new RuntimeException("Restore failed")).when(deletedPostService).restorePost(1L, 2L);

        mockMvc.perform(put("/deletedposts/restore")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Błąd przy przywracaniu posta: Restore failed"));
    }
}

