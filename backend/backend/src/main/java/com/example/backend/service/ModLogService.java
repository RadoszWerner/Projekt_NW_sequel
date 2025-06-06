package com.example.backend.service;

import com.example.backend.dto.ModLogDTO;
import com.example.backend.model.ModLog;
import com.example.backend.repository.CommentRepository;
import com.example.backend.repository.ModLogRepository;
import com.example.backend.repository.PostRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ModLogService {
    private final ModLogRepository modLogRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public ModLogService(
            ModLogRepository modLogRepository,
            UserRepository userRepository,
            CommentRepository commentRepository,
            PostRepository postRepository
    ) {
        this.modLogRepository = modLogRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    public List<ModLogDTO> getAllModLogs() {
        return modLogRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private ModLogDTO mapToDTO(ModLog modLog) {
        ModLogDTO dto = new ModLogDTO();
        dto.setId(modLog.getId());
        dto.setModeratorId(modLog.getModerator().getId());
        dto.setAction(modLog.getAction());
        dto.setActionDetails(modLog.getActionDetails());
        dto.setActionTime(modLog.getActionTime());

        userRepository.findById(modLog.getModerator().getId())
                .ifPresent(user -> dto.setUsername(user.getUsername()));

        if (modLog.getComment() != null) {
            dto.setCommentId(modLog.getComment().getId());
            dto.setCommentContent(modLog.getComment().getContent());
        }

        if (modLog.getPost() != null) {
            dto.setPostId(modLog.getPost().getId());
            dto.setPostTitle(modLog.getPost().getTitle());
            dto.setPostContent(modLog.getPost().getContent());
        }

        return dto;
    }
}