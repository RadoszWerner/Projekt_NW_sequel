package com.example.backend.controller;

import com.example.backend.dto.ModLogDTO;
import com.example.backend.service.ModLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/mod-logs")
public class ModLogController {
    private final ModLogService modLogService;

    public ModLogController(ModLogService modLogService) {
        this.modLogService = modLogService;
    }
    @GetMapping("/all")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<List<ModLogDTO>> getAllModLogs() {
        List<ModLogDTO> logs = modLogService.getAllModLogs();
        return ResponseEntity.ok(logs);
    }
}
