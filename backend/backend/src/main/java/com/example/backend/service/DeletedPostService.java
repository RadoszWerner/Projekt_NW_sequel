package com.example.backend.service;

import com.example.backend.dto.DeletedPostDTO;
import com.example.backend.model.*;
import com.example.backend.model.DeletedPost;
import com.example.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class DeletedPostService {
    private final DeletedPostRepository deletedPostRepository;
    private final PostRepository postRepository;
    private final ModLogRepository modLogRepository;
    private final UserRepository userRepository;

    public DeletedPostService(DeletedPostRepository deletedPostRepository, PostRepository postRepository,
                                 ModLogRepository modLogRepository, UserRepository userRepository) {
        this.deletedPostRepository = deletedPostRepository;
        this.postRepository = postRepository;
        this.modLogRepository = modLogRepository;
        this.userRepository = userRepository;
    }

    public List<DeletedPostDTO> getAllDeletedPosts() {
        return deletedPostRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private DeletedPostDTO mapToDTO(DeletedPost deletedPost) {
        DeletedPostDTO dto = new DeletedPostDTO();
        dto.setId(deletedPost.getId());
        dto.setCommentContent(deletedPost.getPost().getContent());
        dto.setTitleContent(deletedPost.getPost().getTitle());
        dto.setUsername(deletedPost.getPost().getUser().getUsername());
        dto.setDeletedAt(deletedPost.getDeletedAt());
        dto.setReason(deletedPost.getReason());
        dto.setToxic(deletedPost.isToxic());
        dto.setSevereToxic(deletedPost.isSevereToxic());
        dto.setObscene(deletedPost.isObscene());
        dto.setThreat(deletedPost.isThreat());
        dto.setInsult(deletedPost.isInsult());
        dto.setIdentityHate(deletedPost.isIdentityHate());
        return dto;
    }

    @Transactional
    public void restorePost(Long postId, Long modId) {

        DeletedPost deletedpost = deletedPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Usunięty post nie został znaleziony"));
        Post post = deletedpost.getPost();

        post.setDeleted(false);
        post.setToxic(false);
        postRepository.save(post);

        ModLog modLog = new ModLog();
        modLog.setModerator(userRepository.getReferenceById(modId));
        modLog.setPost(post);
        modLog.setAction("restore");
        modLog.setActionDetails("Komentarz przywrócony przez moderatora");
        modLog.setActionTime(LocalDateTime.now());
        modLogRepository.save(modLog);

        deletedPostRepository.delete(deletedpost);
    }
}
