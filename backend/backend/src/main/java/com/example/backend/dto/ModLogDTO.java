package com.example.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ModLogDTO {
    private Long id;
    private Long moderatorId;
    private String username;
    private Long commentId;
    private String commentContent;
    private Long postId;
    private String postTitle;
    private String postContent;
    private String action;
    private String actionDetails;
    private LocalDateTime actionTime;}