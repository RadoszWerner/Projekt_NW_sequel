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
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @Column(nullable = false)
    private String action;

    @Column(columnDefinition = "TEXT")
    private String actionDetails;

    @Column(nullable = false)
    private LocalDateTime actionTime;

}
