package com.example.backend.repository;


import com.example.backend.model.ModLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModLogRepository extends JpaRepository<ModLog, Long> {
}
