package com.example.backend.service;

import com.example.backend.dto.PostDTO;
import com.example.backend.mapper.PostMapper;
import com.example.backend.model.DeletedPost;
import com.example.backend.model.Post;
import com.example.backend.model.User;
import com.example.backend.repository.DeletedPostRepository;
import com.example.backend.repository.PostRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ToxicityCheckService toxicityCheckService;
    private final DeletedPostRepository deletedPostRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository, ToxicityCheckService toxicityCheckService, DeletedPostRepository deletedPostRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.toxicityCheckService = toxicityCheckService;
        this.deletedPostRepository = deletedPostRepository;
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
        post.setDeleted(false);
        post.setToxic(false);

        List<Boolean> scoresContent = toxicityCheckService.getToxicityScores(content);
        List<Boolean> scoresTitle = toxicityCheckService.getToxicityScores(title);
        boolean isTitleToxic = scoresTitle.contains(true);
        boolean isContentToxic = scoresContent.contains(true);
        List<Boolean> scores = new ArrayList<>();
        for (int i = 0; i < scoresContent.size(); i++) {
            scores.add(scoresContent.get(i) || scoresTitle.get(i));
        }
        if (isTitleToxic || isContentToxic) {
            post.setToxic(true);
            post.setDeleted(true);

            // Save the toxic post
            Post savedPost = postRepository.save(post);

            // Save information about deletion
            DeletedPost deletedPost = new DeletedPost();
            deletedPost.setPost(savedPost);
            deletedPost.setModeratedBy(user);  // lub inny użytkownik jeśli masz moderację
            deletedPost.setDeletedAt(LocalDateTime.now());

            String reason = "Post marked as toxic: ";
            if (isTitleToxic) reason += "Title ";
            if (isContentToxic) reason += "Content";
            deletedPost.setReason(reason.trim());
            deletedPost.setToxic(scores.get(0));
            deletedPost.setSevereToxic(scores.get(1));
            deletedPost.setObscene(scores.get(2));
            deletedPost.setThreat(scores.get(3));
            deletedPost.setInsult(scores.get(4));
            deletedPost.setIdentityHate(scores.get(5));
            deletedPostRepository.save(deletedPost);

            return savedPost;
        }
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

    public List<PostDTO> getPostsByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        List<Post> posts = postRepository.findAllByUserId(userId);

        return posts.stream()
                .map(PostMapper::mapToPostDTO) // Use the mapper here
                .collect(Collectors.toList());
    }

    public List<PostDTO> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream()
                .map(PostMapper::mapToPostDTO) // Use the mapper here
                .collect(Collectors.toList());
    }
}
