package com.example.vibely_backend.controller;

import com.example.vibely_backend.entity.LearningTree;
import com.example.vibely_backend.service.LearningTreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/learning-trees")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class LearningTreeController {

    @Autowired
    private LearningTreeService learningTreeService;

    // Tạo cây
    @PostMapping("")
    public ResponseEntity<?> createTree(@RequestBody Map<String, String> request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName(); // Lấy userId từ token
        String treeType = request.get("treeType");

        try {
            LearningTree tree = learningTreeService.createTree(userId, treeType);
            return ResponseEntity.status(201).body(tree);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Lấy cây
    @GetMapping("")
    public ResponseEntity<?> getTree() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName(); // Lấy userId từ token

        Optional<LearningTree> treeOpt = learningTreeService.getTree(userId);

        if (treeOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Bạn chưa có cây học tập"));
        }

        return ResponseEntity.ok(treeOpt.get());
    }

    // Cập nhật loại cây
    @PatchMapping("/type")
    public ResponseEntity<?> updateTreeType(@RequestBody Map<String, String> request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName(); // Lấy userId từ token
        String newTreeType = request.get("treeType");

        try {
            LearningTree tree = learningTreeService.updateTreeType(userId, newTreeType);
            return ResponseEntity.ok(tree);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Lấy cây hiện tại
    @GetMapping("/current")
    public ResponseEntity<?> getCurrentTree() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName(); // Lấy userId từ token

        Optional<LearningTree> treeOpt = learningTreeService.getTree(userId);

        if (treeOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Bạn chưa có cây học tập"));
        }

        return ResponseEntity.ok(treeOpt.get());
    }
}
