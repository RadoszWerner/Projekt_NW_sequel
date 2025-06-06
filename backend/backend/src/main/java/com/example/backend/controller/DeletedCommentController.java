package com.example.backend.controller;

import com.example.backend.dto.DeletedCommentDTO;
import com.example.backend.service.DeletedCommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/deletedcomments")
public class DeletedCommentController {
    private final DeletedCommentService deletedCommentService;

    public DeletedCommentController(DeletedCommentService deletedCommentService) {
        this.deletedCommentService = deletedCommentService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<DeletedCommentDTO>> getDeletedComments() {
        return ResponseEntity.ok(deletedCommentService.getAllDeletedComments());
    }
    @PutMapping("/restore")
    public ResponseEntity<String> restoreComment(@RequestBody Map<String, Long> request) {
        Long commentId = request.get("commentId");
        Long modId = request.get("userId");
        try {
            deletedCommentService.restoreComment(commentId, modId);
            return ResponseEntity.ok("Komentarz przywrócony pomyślnie");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Błąd przy przywracaniu komentarza: " + e.getMessage());
        }
    }

}
