package com.example.vibely_backend.service;

import com.example.vibely_backend.entity.LearningTree;
import com.example.vibely_backend.entity.User;
import com.example.vibely_backend.repository.LearningTreeRepository;
import com.example.vibely_backend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class LearningTreeService {

    @Autowired
    private LearningTreeRepository learningTreeRepository;

    @Autowired
    private UserRepository userRepository;

    // Tạo cây học tập mới
    public LearningTree createTree(String userId, String treeType) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID người dùng không hợp lệ");
        }

        if (treeType == null || treeType.trim().isEmpty()) {
            throw new IllegalArgumentException("Loại cây không được để trống");
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            log.error("Không tìm thấy người dùng với ID: {}", userId);
            throw new IllegalArgumentException("Người dùng không tồn tại");
        }

        if (learningTreeRepository.findByUserId(userId).isPresent()) {
            log.warn("Người dùng {} đã có cây học tập", userId);
            throw new IllegalStateException("Bạn đã có cây học tập");
        }

        if (!isValidTreeType(treeType)) {
            log.error("Loại cây không hợp lệ: {}", treeType);
            throw new IllegalArgumentException("Loại cây không hợp lệ");
        }

        LearningTree tree = new LearningTree();
        tree.setUserId(userId);
        tree.setTreeType(treeType);
        tree.setCompletedGoalsCount(0); // mặc định
        tree.setGrowthStage(LearningTree.TAN_BINH);
        tree.setLastUpdated(LocalDateTime.now());

        LearningTree savedTree = learningTreeRepository.save(tree);
        return savedTree;
    }

    // Lấy thông tin cây học tập
    public Optional<LearningTree> getTree(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID người dùng không hợp lệ");
        }
        return learningTreeRepository.findByUserId(userId);
    }

    // Cập nhật loại cây học tập
    public LearningTree updateTreeType(String userId, String newTreeType) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID người dùng không hợp lệ");
        }

        if (newTreeType == null || newTreeType.trim().isEmpty()) {
            throw new IllegalArgumentException("Loại cây không được để trống");
        }

        if (!isValidTreeType(newTreeType)) {
            log.error("Loại cây không hợp lệ: {}", newTreeType);
            throw new IllegalArgumentException("Loại cây không hợp lệ");
        }

        LearningTree tree = learningTreeRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("Không tìm thấy cây học tập cho người dùng {}", userId);
                    return new IllegalArgumentException("Bạn chưa có cây học tập");
                });

        tree.setTreeType(newTreeType);
        tree.setLastUpdated(LocalDateTime.now());

        LearningTree updatedTree = learningTreeRepository.save(tree);
        return updatedTree;
    }

    // Hàm kiểm tra hợp lệ treeType
    private boolean isValidTreeType(String treeType) {
        return treeType.equals(LearningTree.CACTUS)
                || treeType.equals(LearningTree.GREEN_TREE)
                || treeType.equals(LearningTree.SUNFLOWER);
    }
}
