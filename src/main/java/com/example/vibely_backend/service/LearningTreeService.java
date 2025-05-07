package com.example.vibely_backend.service;

import com.example.vibely_backend.entity.LearningTree;
import com.example.vibely_backend.entity.User;
import com.example.vibely_backend.repository.LearningTreeRepository;
import com.example.vibely_backend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class LearningTreeService {

    @Autowired
    private LearningTreeRepository learningTreeRepository;

    @Autowired
    private UserRepository userRepository;

    // Tạo cây học tập mới
    public LearningTree createTree(String userId, String treeType) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Người dùng không tồn tại");
        }

        if (learningTreeRepository.findByUser_Id(userId).isPresent()) {
            throw new IllegalStateException("Bạn đã có cây học tập");
        }

        if (!isValidTreeType(treeType)) {
            throw new IllegalArgumentException("Loại cây không hợp lệ");
        }

        LearningTree tree = new LearningTree();
        tree.setUser(userOpt.get());
        tree.setTreeType(treeType);
        tree.setCompletedGoalsCount(0); // mặc định
        tree.setGrowthStage(LearningTree.TAN_BINH);
        tree.setLastUpdated(LocalDateTime.now());

        return learningTreeRepository.save(tree);
    }

    // Lấy thông tin cây học tập
    public Optional<LearningTree> getTree(String userId) {
        return learningTreeRepository.findByUser_Id(userId);
    }

    // Cập nhật loại cây học tập
    public LearningTree updateTreeType(String userId, String newTreeType) {
        if (!isValidTreeType(newTreeType)) {
            throw new IllegalArgumentException("Loại cây không hợp lệ");
        }

        LearningTree tree = learningTreeRepository.findByUser_Id(userId)
                .orElseThrow(() -> new IllegalArgumentException("Bạn chưa có cây học tập"));

        tree.setTreeType(newTreeType);
        tree.setLastUpdated(LocalDateTime.now());

        return learningTreeRepository.save(tree);
    }

    // Hàm kiểm tra hợp lệ treeType
    private boolean isValidTreeType(String treeType) {
        return treeType.equals(LearningTree.CACTUS)
                || treeType.equals(LearningTree.GREEN_TREE)
                || treeType.equals(LearningTree.SUNFLOWER);
    }
}
