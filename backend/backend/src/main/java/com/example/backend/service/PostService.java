package com.example.backend.service;

import com.example.backend.dto.PostDTO;
import com.example.backend.mapper.PostMapper;
import com.example.backend.model.Post;
import com.example.backend.model.User;
import com.example.backend.repository.PostRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public Post createPost(String username, String title, String content) {
        // Find the user by username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Create a new post
        Post post = new Post();
        post.setUser(user);
        post.setTitle(title);
        post.setContent(content);
        post.setCreatedAt(LocalDateTime.now());

        return postRepository.save(post);
    }

    public Post editPost(Long postId, String username, String newTitle, String newContent) {
        // Find the post by ID
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        // Check if the username matches the post owner
        if (!post.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("User is not authorized to edit this post");
        }

        // Update the post content, title, and updatedAt timestamp
        post.setTitle(newTitle);
        post.setContent(newContent);
        post.setUpdatedAt(LocalDateTime.now());

        return postRepository.save(post);
    }

    public void deletePost(Long postId, String username) {
        // Find the post by ID
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        // Check if the username matches the post owner
        if (!post.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("User is not authorized to delete this post");
        }

        // Delete the post
        postRepository.delete(post);
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with ID: " + id));
    }

    public List<Post> getPostsByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        return postRepository.findAllByUserId(userId);
    }

    public List<PostDTO> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream()
                .map(PostMapper::mapToPostDTO) // Use the mapper here
                .collect(Collectors.toList());
    }
}
