package com.example.backend.service;

import com.example.backend.dto.DeletedCommentDTO;
import com.example.backend.model.Comment;
import com.example.backend.model.DeletedComment;
import com.example.backend.model.ModLog;
import com.example.backend.repository.CommentRepository;
import com.example.backend.repository.DeletedCommentRepository;
import com.example.backend.repository.ModLogRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeletedCommentService {

    private final DeletedCommentRepository deletedCommentRepository;
    private final CommentRepository commentRepository;
    private final ModLogRepository modLogRepository;
    private final UserRepository userRepository;

    public DeletedCommentService(DeletedCommentRepository deletedCommentRepository,
                                 CommentRepository commentRepository,
                                 ModLogRepository modLogRepository, UserRepository userRepository) {
        this.deletedCommentRepository = deletedCommentRepository;
        this.commentRepository = commentRepository;
        this.modLogRepository = modLogRepository;
        this.userRepository = userRepository;
    }

    public List<DeletedCommentDTO> getAllDeletedComments() {
        return deletedCommentRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private DeletedCommentDTO mapToDTO(DeletedComment deletedComment) {
        DeletedCommentDTO dto = new DeletedCommentDTO();
        dto.setId(deletedComment.getId());
        dto.setCommentContent(deletedComment.getComment().getContent());
        dto.setUsername(deletedComment.getComment().getUser().getUsername());
        dto.setDeletedAt(deletedComment.getDeletedAt());
        dto.setReason(deletedComment.getReason());
        dto.setToxic(deletedComment.isToxic());
        dto.setSevereToxic(deletedComment.isSevereToxic());
        dto.setObscene(deletedComment.isObscene());
        dto.setThreat(deletedComment.isThreat());
        dto.setInsult(deletedComment.isInsult());
        dto.setIdentityHate(deletedComment.isIdentityHate());
        return dto;
    }

    @Transactional
    public void restoreComment(Long commentId, Long modId) {

        DeletedComment deletedComment = deletedCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Usunięty komentarz nie został znaleziony"));
        Comment comment = deletedComment.getComment();
//        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        comment.setIsDeleted(false);
        comment.setToxic(false);
        commentRepository.save(comment);

        ModLog modLog = new ModLog();
        modLog.setModerator(userRepository.getReferenceById(modId));
        modLog.setComment(comment);
        modLog.setAction("restore");
        modLog.setActionDetails("Komentarz przywrócony przez moderatora");
        modLog.setActionTime(LocalDateTime.now());
        modLogRepository.save(modLog);

        deletedCommentRepository.delete(deletedComment);
    }

}
