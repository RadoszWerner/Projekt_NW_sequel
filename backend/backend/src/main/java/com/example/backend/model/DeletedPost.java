package com.example.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class DeletedPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Post post;

    public void setId(Long id) {this.id = id;}

    @ManyToOne
    private User moderatedBy;

    private LocalDateTime deletedAt;

    private String reason;
    @Column(nullable = false)
    private boolean isToxic;
    @Column(nullable = false)
    private boolean isSevereToxic;

    @Column(nullable = false)
    private boolean isObscene;

    @Column(nullable = false)
    private boolean isThreat;

    @Column(nullable = false)
    private boolean isInsult;

    @Column(nullable = false)
    private boolean isIdentityHate;

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


    public Long getId() {
        return id;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public User getModeratedBy() {
        return moderatedBy;
    }

    public void setModeratedBy(User moderatedBy) {
        this.moderatedBy = moderatedBy;
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
}
