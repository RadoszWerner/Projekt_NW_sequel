package com.example.backend.model;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="mod_logs")
public class ModLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "moderator_id", nullable = false)
    private User moderator;

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = true)
    private Comment comment;
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = true)
    private Post post;


    @Column(nullable = false)
    private String action;

    @Column(columnDefinition = "TEXT")
    private String actionDetails;

    @Column(nullable = false)
    private LocalDateTime actionTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getModerator() {
        return moderator;
    }

    public void setModerator(User moderator) {
        this.moderator = moderator;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getActionDetails() {
        return actionDetails;
    }

    public void setActionDetails(String actionDetails) {
        this.actionDetails = actionDetails;
    }

    public LocalDateTime getActionTime() {
        return actionTime;
    }

    public void setActionTime(LocalDateTime actionTime) {
        this.actionTime = actionTime;
    }

}
