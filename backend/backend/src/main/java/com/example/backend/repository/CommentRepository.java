package com.example.backend.repository;

import com.example.backend.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId); // Pobieranie komentarzy dla danego postu

    List<Comment> findByUserId(Long userId); // Pobieranie komentarzy danego użytkownika

    List<Comment> findByIsDeletedTrue();
}