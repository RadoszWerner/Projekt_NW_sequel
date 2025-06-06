package com.example.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class DeletedPostDTO {
    private Long id;
    private String commentContent;

    private String titleContent;
    private String username;
    private LocalDateTime deletedAt;
    private String reason;
    private boolean isToxic;
    private boolean isSevereToxic;
    private boolean isObscene;
    private boolean isThreat;
    private boolean isInsult;
    private boolean isIdentityHate;
}