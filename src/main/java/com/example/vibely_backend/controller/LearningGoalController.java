package com.example.vibely_backend.controller;

import com.example.vibely_backend.dto.request.LearningGoalRequest;
import com.example.vibely_backend.entity.LearningGoal;
import com.example.vibely_backend.service.LearningGoalService;
import com.example.vibely_backend.service.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/learning-goals")
public class LearningGoalController {

    @Autowired
    private LearningGoalService learningGoalService;

    @Autowired
    private JWTService jwtService;

    @PostMapping("")
    public ResponseEntity<?> createGoal(@RequestBody LearningGoalRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userId = jwtService.extractUserIdFromToken(auth.getCredentials().toString());
            LearningGoal goal = learningGoalService.createGoal(userId, request.getTitle());
            return ResponseEntity.status(201).body(goal);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getGoals() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userId = jwtService.extractUserIdFromToken(auth.getCredentials().toString());
            List<LearningGoal> goals = learningGoalService.getGoals(userId);
            return ResponseEntity.ok(goals);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateGoal(@PathVariable String id, @RequestBody LearningGoalRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userId = jwtService.extractUserIdFromToken(auth.getCredentials().toString());
            LearningGoal goal = learningGoalService.updateGoal(userId, id, request.getTitle());
            return ResponseEntity.ok(goal);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGoal(@PathVariable String id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userId = jwtService.extractUserIdFromToken(auth.getCredentials().toString());
            learningGoalService.deleteGoal(userId, id);
            return ResponseEntity.ok(Map.of("message", "Đã xóa mục tiêu"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<?> toggleGoalCompletion(@PathVariable String id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userId = jwtService.extractUserIdFromToken(auth.getCredentials().toString());
            Map<String, Object> result = learningGoalService.toggleGoalCompletion(userId, id);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/visibility")
    public ResponseEntity<?> toggleGoalVisibility(@PathVariable String id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userId = jwtService.extractUserIdFromToken(auth.getCredentials().toString());
            LearningGoal goal = learningGoalService.toggleGoalVisibility(userId, id);
            return ResponseEntity.ok(goal);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}