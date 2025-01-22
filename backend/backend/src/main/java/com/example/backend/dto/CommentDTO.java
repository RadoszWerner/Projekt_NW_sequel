package com.example.backend.dto;


import lombok.Data;

@Data
public class CommentDTO {
    private Long id;
    private String content;
    private String createdAt;
    private Boolean deleted;
    private Boolean toxic;
    private UserDTO user; // User without sensitive fields

    // Getters and setters omitted for brevity
}
