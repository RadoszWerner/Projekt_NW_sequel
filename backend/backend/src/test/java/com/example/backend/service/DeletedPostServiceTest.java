package com.example.backend.service;

import com.example.backend.dto.DeletedPostDTO;
import com.example.backend.model.DeletedPost;
import com.example.backend.model.ModLog;
import com.example.backend.model.Post;
import com.example.backend.model.User;
import com.example.backend.repository.DeletedPostRepository;
import com.example.backend.repository.ModLogRepository;
import com.example.backend.repository.PostRepository;
import com.example.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeletedPostServiceTest {

    @Mock
    private DeletedPostRepository deletedPostRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private ModLogRepository modLogRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DeletedPostService deletedPostService;

    @Test
    void getAllDeletedPosts_ShouldReturnMappedDTOs() {
        // Arrange
        User user = new User();
        user.setUsername("testuser");

        Post post = new Post();
        post.setTitle("Test Title");
        post.setContent("Test Content");
        post.setUser(user);

        DeletedPost deletedPost = new DeletedPost();
        deletedPost.setId(1L);
        deletedPost.setPost(post);
        deletedPost.setDeletedAt(LocalDateTime.now());
        deletedPost.setReason("Toxic");
        deletedPost.setToxic(true);

        when(deletedPostRepository.findAll()).thenReturn(List.of(deletedPost));

        // Act
        List<DeletedPostDTO> result = deletedPostService.getAllDeletedPosts();

        // Assert
        assertEquals(1, result.size());
        DeletedPostDTO dto = result.get(0);
        assertEquals("Test Title", dto.getTitleContent());
        assertEquals("Test Content", dto.getCommentContent());
        assertEquals("testuser", dto.getUsername());
        assertTrue(dto.isToxic());
        assertEquals("Toxic", dto.getReason());
    }

    @Test
    void restorePost_ShouldRestorePostAndLogModeration() {
        // Arrange
        Long postId = 1L;
        Long modId = 2L;

        User moderator = new User();
        moderator.setId(modId);
        moderator.setUsername("mod");

        Post post = new Post();
        post.setDeleted(true);
        post.setToxic(true);

        DeletedPost deletedPost = new DeletedPost();
        deletedPost.setId(postId);
        deletedPost.setPost(post);

        when(deletedPostRepository.findById(postId)).thenReturn(Optional.of(deletedPost));
        when(userRepository.getReferenceById(modId)).thenReturn(moderator);

        // Act
        deletedPostService.restorePost(postId, modId);

        // Assert
        assertFalse(post.getIsDeleted());
        assertFalse(post.getIsToxic());
        verify(postRepository).save(post);
        verify(modLogRepository).save(Mockito.<ModLog>any());
        verify(deletedPostRepository).delete(deletedPost);
    }

    @Test
    void restorePost_ShouldThrow_WhenDeletedPostNotFound() {
        // Arrange
        when(deletedPostRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> deletedPostService.restorePost(99L, 1L));
    }
}
