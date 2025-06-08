package com.example.backend.service;

import com.example.backend.model.Comment;
import com.example.backend.model.DeletedComment;
import com.example.backend.model.Post;
import com.example.backend.model.User;
import com.example.backend.repository.CommentRepository;
import com.example.backend.repository.DeletedCommentRepository;
import com.example.backend.repository.PostRepository;
import com.example.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private ToxicityCheckService toxicityCheckService;

    @Mock
    private DeletedCommentRepository deletedCommentRepository;

    @InjectMocks
    private CommentService commentService;

    private User user;
    private Post post;
    private Comment comment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set up a mock user
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        // Set up a mock post
        post = new Post();
        post.setId(1L);
        post.setTitle("Test Post");
        post.setContent("This is a test post");
        post.setUser(user);

        // Set up a mock comment
        comment = new Comment();
        comment.setId(1L);
        comment.setContent("This is a test comment");
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUser(user);
        comment.setPost(post);
        comment.setDeleted(false);
        comment.setToxic(false);
    }

    @Test
    void addComment_ShouldAddNonToxicComment() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(toxicityCheckService.isToxic("This is a test comment")).thenReturn(false);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        // Act
        Comment result = commentService.addComment("testuser", 1L, "This is a test comment");

        // Assert
        assertNotNull(result);
        assertEquals("This is a test comment", result.getContent());
        verify(commentRepository, times(1)).save(any(Comment.class));
        verify(deletedCommentRepository, never()).save(any());
    }

    @Test
    void addComment_ShouldAddToxicCommentAndMarkDeleted() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(toxicityCheckService.getToxicityScores("Toxic comment"))
                .thenReturn(List.of(true, false, false, false, false, false));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
            Comment savedComment = invocation.getArgument(0);
            assertTrue(savedComment.getIsToxic());
            assertTrue(savedComment.getIsDeleted());
            return savedComment;
        });

        // Act
        Comment result = commentService.addComment("testuser", 1L, "Toxic comment");

        // Assert
        assertNotNull(result);
        assertTrue(result.getIsDeleted());
        assertTrue(result.getIsToxic());
        verify(deletedCommentRepository, times(1)).save(any(DeletedComment.class));
    }


    @Test
    void editComment_ShouldUpdateContent_WhenUserIsOwner() {
        // Arrange
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Mock save

        // Act
        Comment result = commentService.editComment(1L, "testuser", "Updated comment content");

        // Assert
        assertNotNull(result);
        assertEquals("Updated comment content", result.getContent());
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void editComment_ShouldThrowException_WhenUserIsNotOwner() {
        // Arrange
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                commentService.editComment(1L, "wronguser", "Updated comment content"));
        assertEquals("User is not authorized to edit this comment", exception.getMessage());
    }

    @Test
    void deleteComment_ShouldDeleteComment_WhenUserIsOwner() {
        // Arrange
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        // Act
        commentService.deleteComment(1L, "testuser");

        // Assert
        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    void deleteComment_ShouldThrowException_WhenUserIsNotOwner() {
        // Arrange
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                commentService.deleteComment(1L, "wronguser"));
        assertEquals("User is not authorized to delete this comment", exception.getMessage());
        verify(commentRepository, never()).delete(any());
    }
}

