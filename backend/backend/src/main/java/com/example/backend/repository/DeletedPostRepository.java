package com.example.backend.repository;

import com.example.backend.model.DeletedPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeletedPostRepository extends JpaRepository<DeletedPost, Long> {
}
