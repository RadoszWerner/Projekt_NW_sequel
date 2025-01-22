package com.example.backend.service;

import com.example.backend.model.Post;
import com.example.backend.model.User;
import com.example.backend.repository.PostRepository;
import com.example.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createPost_ShouldReturnPost_WhenUserExists() {
        // Arrange
        String username = "testuser";
        String title = "Post Title";
        String content = "Post Content";

        User user = new User();
        user.setUsername(username);

        Post savedPost = new Post();
        savedPost.setId(1L);
        savedPost.setTitle(title);
        savedPost.setContent(content);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(postRepository.save(any(Post.class))).thenReturn(savedPost);

        // Act
        Post result = postService.createPost(username, title, content);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(title, result.getTitle());
        assertEquals(content, result.getContent());
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void createPost_ShouldThrowException_WhenUserDoesNotExist() {
        // Arrange
        String username = "testuser";
        String title = "Post Title";
        String content = "Post Content";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                postService.createPost(username, title, content)
        );

        assertEquals("User not found", exception.getMessage());
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void editPost_ShouldReturnUpdatedPost_WhenUserIsOwner() {
        // Arrange
        Long postId = 1L;
        String username = "testuser";
        String newTitle = "Updated Title";
        String newContent = "Updated Content";

        User user = new User();
        user.setUsername(username);

        Post post = new Post();
        post.setId(postId);
        post.setUser(user);
        post.setTitle("Original Title");
        post.setContent("Original Content");

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);

        // Act
        Post result = postService.editPost(postId, username, newTitle, newContent);

        // Assert
        assertNotNull(result);
        assertEquals(newTitle, result.getTitle());
        assertEquals(newContent, result.getContent());
        verify(postRepository).save(post);
    }

    @Test
    void editPost_ShouldThrowException_WhenUserIsNotOwner() {
        // Arrange
        Long postId = 1L;
        String username = "otheruser";
        String newTitle = "Updated Title";
        String newContent = "Updated Content";

        User owner = new User();
        owner.setUsername("testuser");

        Post post = new Post();
        post.setId(postId);
        post.setUser(owner);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                postService.editPost(postId, username, newTitle, newContent)
        );

        assertEquals("User is not authorized to edit this post", exception.getMessage());
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void deletePost_ShouldCallRepositoryDelete_WhenUserIsOwner() {
        // Arrange
        Long postId = 1L;
        String username = "testuser";

        User user = new User();
        user.setUsername(username);

        Post post = new Post();
        post.setId(postId);
        post.setUser(user);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // Act
        postService.deletePost(postId, username);

        // Assert
        verify(postRepository).delete(post);
    }

    @Test
    void deletePost_ShouldThrowException_WhenUserIsNotOwner() {
        // Arrange
        Long postId = 1L;
        String username = "otheruser";

        User owner = new User();
        owner.setUsername("testuser");

        Post post = new Post();
        post.setId(postId);
        post.setUser(owner);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                postService.deletePost(postId, username)
        );

        assertEquals("User is not authorized to delete this post", exception.getMessage());
        verify(postRepository, never()).delete(any(Post.class));
    }
}
