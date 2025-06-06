package com.example.backend.dto;

import lombok.Data;

@Data
public class PostDTO {
    private Long id;
    private String title;
    private String content;
    private String createdAt;
    private String updatedAt;
    private boolean isToxic;
    private boolean isDeleted;
    private UserDTO user;

    // Getters and setters
}
