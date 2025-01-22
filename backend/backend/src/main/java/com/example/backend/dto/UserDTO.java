package com.example.backend.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private Boolean isModerator;

    // Getters and setters
}