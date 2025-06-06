package com.example.backend.dto;

import java.time.LocalDateTime;

public class DeletedCommentDTO {
    private Long id;
    private String commentContent;
    private String username;
    private LocalDateTime deletedAt;
    private String reason;

    private boolean isToxic;
    private boolean isSevereToxic;
    private boolean isObscene;
    private boolean isThreat;
    private boolean isInsult;
    private boolean isIdentityHate;

    // Gettery i settery

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isToxic() {
        return isToxic;
    }

    public void setToxic(boolean toxic) {
        isToxic = toxic;
    }

    public boolean isSevereToxic() {
        return isSevereToxic;
    }

    public void setSevereToxic(boolean severeToxic) {
        isSevereToxic = severeToxic;
    }

    public boolean isObscene() {
        return isObscene;
    }

    public void setObscene(boolean obscene) {
        isObscene = obscene;
    }

    public boolean isThreat() {
        return isThreat;
    }

    public void setThreat(boolean threat) {
        isThreat = threat;
    }

    public boolean isInsult() {
        return isInsult;
    }

    public void setInsult(boolean insult) {
        isInsult = insult;
    }

    public boolean isIdentityHate() {
        return isIdentityHate;
    }

    public void setIdentityHate(boolean identityHate) {
        isIdentityHate = identityHate;
    }
}
