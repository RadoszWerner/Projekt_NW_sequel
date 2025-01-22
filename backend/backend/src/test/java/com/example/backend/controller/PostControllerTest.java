package com.example.backend.controller;

import com.example.backend.model.Post;
import com.example.backend.service.JWTService;
import com.example.backend.service.PostService;
import com.example.backend.service.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
@AutoConfigureMockMvc(addFilters = false)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JWTService jwtService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        Mockito.reset(postService);
    }

    @Test
    void createPost_ShouldReturnSuccessMessage_WhenValidRequest() throws Exception {
        // Arrange
        Map<String, String> payload = new HashMap<>();
        payload.put("username", "testuser");
        payload.put("title", "Post Title");
        payload.put("content", "Post Content");

        Post createdPost = new Post();
        createdPost.setId(1L);

        Mockito.when(postService.createPost("testuser", "Post Title", "Post Content")).thenReturn(createdPost);

        // Act & Assert
        mockMvc.perform(post("/posts/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(content().string("Post created successfully with ID: 1"));
    }

    @Test
    void createPost_ShouldReturnBadRequest_WhenUserNotFound() throws Exception {
        // Arrange
        Map<String, String> payload = new HashMap<>();
        payload.put("username", "unknownuser");
        payload.put("title", "Post Title");
        payload.put("content", "Post Content");

        Mockito.when(postService.createPost(eq("unknownuser"), eq("Post Title"), eq("Post Content")))
                .thenThrow(new IllegalArgumentException("User not found"));

        // Act & Assert
        mockMvc.perform(post("/posts/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));
    }

    @Test
    void editPost_ShouldReturnSuccessMessage_WhenValidRequest() throws Exception {
        // Arrange
        Map<String, Object> payload = new HashMap<>();
        payload.put("postId", 1L);
        payload.put("username", "testuser");
        payload.put("newTitle", "Updated Title");
        payload.put("newContent", "Updated Content");

        Mockito.when(postService.editPost(1L, "testuser", "Updated Title", "Updated Content"))
                .thenReturn(new Post());

        // Act & Assert
        mockMvc.perform(put("/posts/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(content().string("Post updated successfully"));
    }

    @Test
    void editPost_ShouldReturnBadRequest_WhenUserIsNotOwner() throws Exception {
        // Arrange
        Map<String, Object> payload = new HashMap<>();
        payload.put("postId", 1L);
        payload.put("username", "otheruser");
        payload.put("newTitle", "Updated Title");
        payload.put("newContent", "Updated Content");

        Mockito.when(postService.editPost(eq(1L), eq("otheruser"), eq("Updated Title"), eq("Updated Content")))
                .thenThrow(new IllegalArgumentException("User is not authorized to edit this post"));

        // Act & Assert
        mockMvc.perform(put("/posts/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User is not authorized to edit this post"));
    }

    @Test
    void deletePost_ShouldReturnSuccessMessage_WhenValidRequest() throws Exception {
        // Arrange
        Map<String, Object> payload = new HashMap<>();
        payload.put("postId", 1L);
        payload.put("username", "testuser");

        // Act & Assert
        mockMvc.perform(delete("/posts/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(content().string("Post deleted successfully"));

        Mockito.verify(postService).deletePost(1L, "testuser");
    }

    @Test
    void deletePost_ShouldReturnBadRequest_WhenUserIsNotOwner() throws Exception {
        // Arrange
        Map<String, Object> payload = new HashMap<>();
        payload.put("postId", 1L);
        payload.put("username", "otheruser");

        Mockito.doThrow(new IllegalArgumentException("User is not authorized to delete this post"))
                .when(postService).deletePost(eq(1L), eq("otheruser"));

        // Act & Assert
        mockMvc.perform(delete("/posts/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User is not authorized to delete this post"));
    }
}
