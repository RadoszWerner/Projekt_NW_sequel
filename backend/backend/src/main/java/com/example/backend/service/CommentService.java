package com.example.backend.service;

import com.example.backend.model.Comment;
import com.example.backend.model.DeletedComment;
import com.example.backend.model.Post;
import com.example.backend.model.User;
import com.example.backend.repository.CommentRepository;
import com.example.backend.repository.DeletedCommentRepository;
import com.example.backend.repository.PostRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ToxicityCheckService toxicityCheckService;
    private final DeletedCommentRepository deletedCommentRepository;

    public CommentService(CommentRepository commentRepository, UserRepository userRepository, PostRepository postRepository, ToxicityCheckService toxicityCheckService, DeletedCommentRepository deletedCommentRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.toxicityCheckService = toxicityCheckService;
        this.deletedCommentRepository = deletedCommentRepository;
    }

    public Comment addComment(String username, Long postId, String content) {
        // Find the user by username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Find the post by ID
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        // Create a new comment
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setPost(post);
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setDeleted(false);
        comment.setToxic(false);

        // Check if the content is toxic
        if (toxicityCheckService.isToxic(content)) {
            // Set flags for the comment
            comment.setToxic(true);
            comment.setDeleted(true);

            // Save the comment in the comments table
            Comment savedComment = commentRepository.save(comment);

            // Save the comment in the deleted_comments table
            DeletedComment deletedComment = new DeletedComment();
            deletedComment.setComment(savedComment);
            deletedComment.setModeratedBy(user);
            deletedComment.setDeletedAt(LocalDateTime.now());
            deletedComment.setReason("Comment marked as toxic");
            deletedCommentRepository.save(deletedComment);

            return savedComment;
        }

        // Save the comment if it's not toxic
        return commentRepository.save(comment);
    }

    public Comment editComment(Long commentId, String username, String newContent) {
        // Find the comment by ID
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        // Check if the username matches the comment owner
        if (!comment.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("User is not authorized to edit this comment");
        }

        // Update the comment content and timestamp
        comment.setContent(newContent);
        comment.setUpdatedAt(LocalDateTime.now());

        return commentRepository.save(comment);
    }

    public void deleteComment(Long commentId, String username) {
        // Find the comment by ID
        var comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        // Check if the username matches the comment owner
        if (!comment.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("User is not authorized to delete this comment");
        }

        // Delete the comment
        commentRepository.delete(comment);
    }

    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    public List<Comment> getDeletedComments() {
        return commentRepository.findByIsDeletedTrue();
    }

    public Comment restoreDeletedComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        comment.setDeleted(false);
        comment.setToxic(false);
        return commentRepository.save(comment);
    }
}
