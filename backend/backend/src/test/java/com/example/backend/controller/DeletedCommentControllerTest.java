package com.example.backend.controller;

import com.example.backend.dto.DeletedCommentDTO;
import com.example.backend.filter.JWTAuthenticationFilter;
import com.example.backend.service.DeletedCommentService;
import com.example.backend.service.JWTService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.util.List;
import java.util.Map;


import static org.mockito.Mockito.*;
import static org.springframework.http.RequestEntity.put;
// import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
// import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(DeletedCommentController.class)
@AutoConfigureMockMvc(addFilters = false)  // Disable security filters if any
class DeletedCommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DeletedCommentService deletedCommentService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JWTAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JWTService jwtService;

    @Test
    void getDeletedComments_ShouldReturnListOfDeletedCommentDTOs() throws Exception {
        DeletedCommentDTO dto = new DeletedCommentDTO();
        dto.setId(1L);
        dto.setReason("Spam");

        when(deletedCommentService.getAllDeletedComments()).thenReturn(List.of(dto));

        mockMvc.perform(get("/deletedcomments/all"))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.size()").value(1))
                .andExpect((ResultMatcher) jsonPath("$[0].id").value(1L))
                .andExpect((ResultMatcher) jsonPath("$[0].reason").value("Spam"));
    }

    @Test
    void restoreComment_ShouldReturnSuccessMessage_WhenRestoreSucceeds() throws Exception {
        Map<String, Long> request = Map.of("commentId", 10L, "userId", 5L);

        // Just do nothing on service restore (no exception means success)
        doNothing().when(deletedCommentService).restoreComment(10L, 5L);

        mockMvc.perform(MockMvcRequestBuilders.put("/deletedcomments/restore")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) content().string("Komentarz przywrócony pomyślnie"));
    }

    @Test
    void restoreComment_ShouldReturnErrorMessage_WhenRestoreThrows() throws Exception {
        Map<String, Long> request = Map.of("commentId", 10L, "userId", 5L);

        doThrow(new RuntimeException("restore failed"))
                .when(deletedCommentService).restoreComment(10L, 5L);

        mockMvc.perform(MockMvcRequestBuilders.put("/deletedcomments/restore")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect((ResultMatcher) content().string("Błąd przy przywracaniu komentarza: restore failed"));
    }
}
