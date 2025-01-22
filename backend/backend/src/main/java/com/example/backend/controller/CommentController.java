package com.example.backend.controller;

import com.example.backend.dto.CommentDTO;
import com.example.backend.mapper.CommentMapper;
import com.example.backend.model.Comment;
import com.example.backend.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addComment(@RequestBody Map<String, Object> payload) {
        try {
            String username = (String) payload.get("username");
            Long postId = Long.valueOf(payload.get("postId").toString());
            String content = (String) payload.get("content");

            Comment comment = commentService.addComment(username, postId, content);
            return ResponseEntity.ok("Comment added successfully with ID: " + comment.getId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/edit")
    public ResponseEntity<String> editComment(@RequestBody Map<String, Object> payload) {
        try {
            Long commentId = Long.valueOf(payload.get("commentId").toString());
            String username = (String) payload.get("username");
            String newContent = (String) payload.get("newContent");

            Comment comment = commentService.editComment(commentId, username, newContent);
            return ResponseEntity.ok("Comment updated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteComment(@RequestBody Map<String, Object> payload) {
        try {
            Long commentId = Long.valueOf(payload.get("commentId").toString());
            String username = (String) payload.get("username");

            commentService.deleteComment(commentId, username);
            return ResponseEntity.ok("Comment deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByPostId(@PathVariable Long postId) {
        try {
            List<Comment> comments = commentService.getCommentsByPostId(postId);
            List<CommentDTO> commentDTOs = comments.stream()
                    .map(CommentMapper::mapToCommentDTO) // Map each comment to CommentDTO
                    .collect(Collectors.toList());
            return ResponseEntity.ok(commentDTOs);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/deleted")
    public ResponseEntity<List<Comment>> getDeletedComments() {
        List<Comment> deletedComments = commentService.getDeletedComments();
        return ResponseEntity.ok(deletedComments);
    }

    @PutMapping("/restore")
    public ResponseEntity<String> restoreComment(@RequestBody Map<String, Long> payload) {
        try {
            Long commentId = payload.get("commentId");
            commentService.restoreDeletedComment(commentId);
            System.out.println(commentId);
            return ResponseEntity.ok("Comment restored successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}

