package com.example.backend.repository;

import com.example.backend.model.DeletedComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeletedCommentRepository extends JpaRepository<DeletedComment, Long> {
}
