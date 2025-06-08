package com.example.backend.service;

import com.example.backend.dto.PostDTO;
import com.example.backend.model.DeletedPost;
import com.example.backend.model.Post;
import com.example.backend.model.User;
import com.example.backend.repository.DeletedPostRepository;
import com.example.backend.repository.PostRepository;
import com.example.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ToxicityCheckService toxicityCheckService;

    @Mock
    private DeletedPostRepository deletedPostRepository;


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
        when(toxicityCheckService.getToxicityScores(anyString())).thenReturn(List.of(false, false, false, false, false, false));


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

    @Test
    void createPost_ShouldMarkPostAsToxic_WhenToxicityDetected() {
        // Arrange
        String username = "testuser";
        String title = "Toxic Title";
        String content = "Toxic Content";

        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        List<Boolean> toxicScores = List.of(true, true, true, true, true, true);
        when(toxicityCheckService.getToxicityScores(anyString()))
                .thenReturn(toxicScores) // for content
                .thenReturn(toxicScores); // for title

        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act & Assert
        Post result = postService.createPost(username, title, content);
        assertNotNull(result);
        assertTrue(result.getIsToxic());
        assertTrue(result.getIsDeleted());

        verify(deletedPostRepository).save(any(DeletedPost.class));
    }



    @Test
    void getPostById_ShouldReturnPost_WhenFound() {
        // Arrange
        Long postId = 1L;
        Post post = new Post();
        post.setId(postId);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // Act
        Post result = postService.getPostById(postId);

        // Assert
        assertNotNull(result);
        assertEquals(postId, result.getId());
    }

    @Test
    void getPostById_ShouldThrowException_WhenNotFound() {
        // Arrange
        Long postId = 1L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                postService.getPostById(postId)
        );
        assertEquals("Post not found with ID: " + postId, exception.getMessage());
    }

    @Test
    void getPostsByUserId_ShouldReturnPostDTOList_WhenUserIdIsValid() {
        // Arrange
        Long userId = 1L;
        Post post = new Post();
        post.setId(1L);
        post.setTitle("Title");
        post.setContent("Content");
        post.setUser(new User());
        post.setCreatedAt(LocalDateTime.now());


        when(postRepository.findAllByUserId(userId)).thenReturn(List.of(post));

        // Act
        List<PostDTO> result = postService.getPostsByUserId(userId);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Title", result.get(0).getTitle());
    }

    @Test
    void getPostsByUserId_ShouldThrowException_WhenUserIdIsNull() {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                postService.getPostsByUserId(null)
        );
        assertEquals("User ID cannot be null", exception.getMessage());
    }

    @Test
    void getAllPosts_ShouldReturnAllPostDTOs() {
        // Arrange
        Post post1 = new Post();
        post1.setId(1L);
        post1.setTitle("Post 1");
        post1.setContent("Content 1");
        post1.setUser(new User());
        post1.setCreatedAt(LocalDateTime.now());


        Post post2 = new Post();
        post2.setId(2L);
        post2.setTitle("Post 2");
        post2.setContent("Content 2");
        post2.setUser(new User());
        post2.setCreatedAt(LocalDateTime.now());


        when(postRepository.findAll()).thenReturn(List.of(post1, post2));

        // Act
        List<PostDTO> result = postService.getAllPosts();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Post 1", result.get(0).getTitle());
        assertEquals("Post 2", result.get(1).getTitle());
    }




}
