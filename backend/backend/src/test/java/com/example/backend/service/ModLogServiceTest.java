package com.example.backend.service;

import com.example.backend.dto.ModLogDTO;
import com.example.backend.model.Comment;
import com.example.backend.model.ModLog;
import com.example.backend.model.Post;
import com.example.backend.model.User;
import com.example.backend.repository.CommentRepository;
import com.example.backend.repository.ModLogRepository;
import com.example.backend.repository.PostRepository;
import com.example.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModLogServiceTest {

    @Mock
    private ModLogRepository modLogRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private ModLogService modLogService;

    @Test
    void getAllModLogs_ShouldReturnMappedDTOs_WhenLogsExist() {
        // Mock User
        User moderator = new User();
        moderator.setId(1L);
        moderator.setUsername("modUser");

        // Mock Comment
        Comment comment = new Comment();
        comment.setId(10L);
        comment.setContent("Test comment");

        // Mock Post
        Post post = new Post();
        post.setId(20L);
        post.setTitle("Test Post");
        post.setContent("Post content");

        // Mock ModLog
        ModLog modLog = new ModLog();
        modLog.setId(100L);
        modLog.setModerator(moderator);
        modLog.setComment(comment);
        modLog.setPost(post);
        modLog.setAction("restore");
        modLog.setActionDetails("Restored post");
        modLog.setActionTime(LocalDateTime.of(2023, 1, 1, 12, 0));

        when(modLogRepository.findAll()).thenReturn(List.of(modLog));
        when(userRepository.findById(1L)).thenReturn(Optional.of(moderator));

        // Call the method
        List<ModLogDTO> results = modLogService.getAllModLogs();

        // Verify
        assertEquals(1, results.size());
        ModLogDTO dto = results.get(0);
        assertEquals(100L, dto.getId());
        assertEquals("modUser", dto.getUsername());
        assertEquals("restore", dto.getAction());
        assertEquals("Restored post", dto.getActionDetails());
        assertEquals(LocalDateTime.of(2023, 1, 1, 12, 0), dto.getActionTime());
        assertEquals(10L, dto.getCommentId());
        assertEquals("Test comment", dto.getCommentContent());
        assertEquals(20L, dto.getPostId());
        assertEquals("Test Post", dto.getPostTitle());
        assertEquals("Post content", dto.getPostContent());
    }

    @Test
    void getAllModLogs_ShouldHandleMissingUser() {
        User moderator = new User();
        moderator.setId(2L); // no username will be returned

        ModLog modLog = new ModLog();
        modLog.setId(101L);
        modLog.setModerator(moderator);
        modLog.setAction("delete");
        modLog.setActionDetails("Deleted comment");
        modLog.setActionTime(LocalDateTime.now());

        when(modLogRepository.findAll()).thenReturn(List.of(modLog));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        List<ModLogDTO> results = modLogService.getAllModLogs();

        assertEquals(1, results.size());
        ModLogDTO dto = results.get(0);
        assertEquals(101L, dto.getId());
        assertNull(dto.getUsername()); // username not set
    }

    @Test
    void getAllModLogs_ShouldReturnEmptyList_WhenNoLogs() {
        when(modLogRepository.findAll()).thenReturn(Collections.emptyList());

        List<ModLogDTO> results = modLogService.getAllModLogs();

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }
}



