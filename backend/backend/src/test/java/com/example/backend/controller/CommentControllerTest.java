package com.example.backend.controller;

import com.example.backend.model.Comment;
import com.example.backend.service.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CommentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
    }

    @Test
    void addComment_ShouldReturnSuccessResponse_WhenValidRequest() throws Exception {
        // Arrange
        Map<String, Object> payload = new HashMap<>();
        payload.put("username", "testuser");
        payload.put("postId", 1L);
        payload.put("content", "Sample comment");

        Comment comment = new Comment();
        comment.setId(1L);

        when(commentService.addComment("testuser", 1L, "Sample comment")).thenReturn(comment);

        // Act & Assert
        mockMvc.perform(post("/comments/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(content().string("Comment added successfully with ID: 1"));
    }

    @Test
    void addComment_ShouldReturnBadRequest_WhenServiceThrowsException() throws Exception {
        // Arrange
        Map<String, Object> payload = new HashMap<>();
        payload.put("username", "testuser");
        payload.put("postId", 1L);
        payload.put("content", "Sample comment");

        when(commentService.addComment("testuser", 1L, "Sample comment"))
                .thenThrow(new IllegalArgumentException("User not found"));

        // Act & Assert
        mockMvc.perform(post("/comments/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));
    }

    @Test
    void editComment_ShouldReturnSuccessResponse_WhenValidRequest() throws Exception {
        // Arrange
        Map<String, Object> payload = new HashMap<>();
        payload.put("commentId", 1L);
        payload.put("username", "testuser");
        payload.put("newContent", "Updated comment");

        Comment comment = new Comment();
        comment.setId(1L);

        when(commentService.editComment(1L, "testuser", "Updated comment")).thenReturn(comment);

        // Act & Assert
        mockMvc.perform(put("/comments/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(content().string("Comment updated successfully"));
    }

    @Test
    void editComment_ShouldReturnBadRequest_WhenServiceThrowsException() throws Exception {
        // Arrange
        Map<String, Object> payload = new HashMap<>();
        payload.put("commentId", 1L);
        payload.put("username", "testuser");
        payload.put("newContent", "Updated comment");

        when(commentService.editComment(1L, "testuser", "Updated comment"))
                .thenThrow(new IllegalArgumentException("Comment not found"));

        // Act & Assert
        mockMvc.perform(put("/comments/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Comment not found"));
    }

    @Test
    void deleteComment_ShouldReturnSuccessResponse_WhenValidRequest() throws Exception {
        // Arrange
        Map<String, Object> payload = new HashMap<>();
        payload.put("commentId", 1L);
        payload.put("username", "testuser");

        doNothing().when(commentService).deleteComment(1L, "testuser");

        // Act & Assert
        mockMvc.perform(delete("/comments/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(content().string("Comment deleted successfully"));
    }

    @Test
    void deleteComment_ShouldReturnBadRequest_WhenServiceThrowsException() throws Exception {
        // Arrange
        Map<String, Object> payload = new HashMap<>();
        payload.put("commentId", 1L);
        payload.put("username", "testuser");

        doThrow(new IllegalArgumentException("Comment not found"))
                .when(commentService).deleteComment(1L, "testuser");

        // Act & Assert
        mockMvc.perform(delete("/comments/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Comment not found"));
    }
}
