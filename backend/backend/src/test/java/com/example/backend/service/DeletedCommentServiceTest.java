package com.example.backend.service;

import com.example.backend.dto.DeletedCommentDTO;
import com.example.backend.model.Comment;
import com.example.backend.model.DeletedComment;
import com.example.backend.model.ModLog;
import com.example.backend.model.User;
import com.example.backend.repository.CommentRepository;
import com.example.backend.repository.DeletedCommentRepository;
import com.example.backend.repository.ModLogRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeletedCommentServiceTest {

    @Mock
    private DeletedCommentRepository deletedCommentRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ModLogRepository modLogRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DeletedCommentService deletedCommentService;

    @Test
    void getAllDeletedComments_ShouldReturnListOfDeletedCommentDTOs() {
        // Arrange
        User user = new User();
        user.setUsername("moderator");

        Comment comment = new Comment();
        comment.setContent("This is toxic");
        comment.setUser(user);

        DeletedComment deletedComment = new DeletedComment();
        deletedComment.setId(1L);
        deletedComment.setComment(comment);
        deletedComment.setDeletedAt(LocalDateTime.now());
        deletedComment.setReason("Toxic");
        deletedComment.setToxic(true);
        deletedComment.setSevereToxic(false);
        deletedComment.setObscene(true);
        deletedComment.setThreat(false);
        deletedComment.setInsult(true);
        deletedComment.setIdentityHate(false);

        when(deletedCommentRepository.findAll()).thenReturn(List.of(deletedComment));

        // Act
        List<DeletedCommentDTO> result = deletedCommentService.getAllDeletedComments();

        // Assert
        assertEquals(1, result.size());
        DeletedCommentDTO dto = result.get(0);
        assertEquals("This is toxic", dto.getCommentContent());
        assertEquals("moderator", dto.getUsername());
        assertEquals("Toxic", dto.getReason());
        assertTrue(dto.isToxic());
        assertTrue(dto.isObscene());
        assertTrue(dto.isInsult());
        assertFalse(dto.isSevereToxic());
        assertFalse(dto.isThreat());
        assertFalse(dto.isIdentityHate());
    }

    @Test
    void restoreComment_ShouldRestoreCommentAndLogAction() {
        // Arrange
        Long commentId = 1L;
        Long modId = 100L;

        User moderator = new User();
        moderator.setId(modId);
        moderator.setUsername("mod");

        Comment comment = new Comment();
        comment.setIsDeleted(true);
        comment.setToxic(true);
        comment.setUser(moderator);

        DeletedComment deletedComment = new DeletedComment();
        deletedComment.setId(commentId);
        deletedComment.setComment(comment);

        when(deletedCommentRepository.findById(commentId)).thenReturn(Optional.of(deletedComment));
        when(userRepository.getReferenceById(modId)).thenReturn(moderator);

        // Act
        deletedCommentService.restoreComment(commentId, modId);

        // Assert
        assertFalse(comment.getIsDeleted());
        assertFalse(comment.getIsToxic());
        verify(commentRepository).save(comment);
        verify(deletedCommentRepository).delete(deletedComment);
        verify(modLogRepository).save(Mockito.<ModLog>any());
    }

    @Test
    void restoreComment_ShouldThrowException_WhenDeletedCommentNotFound() {
        // Arrange
        when(deletedCommentRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> deletedCommentService.restoreComment(1L, 2L));
        assertEquals("Usunięty komentarz nie został znaleziony", exception.getMessage());
    }
}
