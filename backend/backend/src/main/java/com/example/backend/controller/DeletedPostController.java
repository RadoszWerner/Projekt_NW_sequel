package com.example.backend.controller;

import com.example.backend.dto.DeletedPostDTO;
import com.example.backend.service.DeletedPostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/deletedposts")
public class DeletedPostController {
    private final DeletedPostService deletedPostService;

    public DeletedPostController(DeletedPostService deletedPostService) {
        this.deletedPostService = deletedPostService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<DeletedPostDTO>> getDeletedPosts() {
        return ResponseEntity.ok(deletedPostService.getAllDeletedPosts());
    }
    @PutMapping("/restore")
    public ResponseEntity<String> restoreComment(@RequestBody Map<String, Long> request) {
        Long postId = request.get("postId");
        Long modId = request.get("userId");
        try {
            deletedPostService.restorePost(postId, modId);
            return ResponseEntity.ok("Post przywrócony pomyślnie");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Błąd przy przywracaniu posta: " + e.getMessage());
        }
    }
}
