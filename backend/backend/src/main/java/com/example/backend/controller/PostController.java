package com.example.backend.controller;

import com.example.backend.dto.PostDTO;
import com.example.backend.mapper.PostMapper;
import com.example.backend.model.Post;
import com.example.backend.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createPost(@RequestBody Map<String, String> payload) {
        try {
            String username = payload.get("username");
            String title = payload.get("title");
            String content = payload.get("content");

            Post post = postService.createPost(username, title, content);
            System.out.println("Payload: " + payload);
            System.out.println("Post ID: " + post.getId());
            return ResponseEntity.ok("Post created successfully with ID: " + post.getId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/edit")
    public ResponseEntity<String> editPost(@RequestBody Map<String, Object> payload) {
        try {
            Long postId = Long.valueOf(payload.get("postId").toString());
            String username = (String) payload.get("username");
            String newTitle = (String) payload.get("newTitle");
            String newContent = (String) payload.get("newContent");

            Post post = postService.editPost(postId, username, newTitle, newContent);
            return ResponseEntity.ok("Post updated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deletePost(@RequestBody Map<String, Object> payload) {
        try {
            Long postId = Long.valueOf(payload.get("postId").toString());
            String username = (String) payload.get("username");

            postService.deletePost(postId, username);
            return ResponseEntity.ok("Post deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<PostDTO>> getAllPosts() {
        System.out.println("Received request to fetch all posts");
        try {
            List<PostDTO> posts = postService.getAllPosts();
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long id) {
        try {
            Post post = postService.getPostById(id);
            PostDTO postDTO = PostMapper.mapToPostDTO(post); // Map entity to DTO
            return ResponseEntity.ok(postDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostDTO>> getPostsByUserId(@PathVariable Long userId) {
        try {
            List<PostDTO> posts = postService.getPostsByUserId(userId);
            return ResponseEntity.ok(posts);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
}
