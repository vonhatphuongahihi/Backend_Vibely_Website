package com.example.vibely_backend.service;

import com.example.vibely_backend.entity.LearningGoal;
import com.example.vibely_backend.entity.LearningTree;
import com.example.vibely_backend.entity.Achievement;
import com.example.vibely_backend.entity.User;
import com.example.vibely_backend.repository.LearningGoalRepository;
import com.example.vibely_backend.repository.LearningTreeRepository;
import com.example.vibely_backend.repository.AchievementRepository;
import com.example.vibely_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@Service
public class LearningGoalService {

    @Autowired
    private LearningGoalRepository learningGoalRepository;

    @Autowired
    private LearningTreeRepository learningTreeRepository;

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private UserRepository userRepository;

    public LearningGoal createGoal(String userId, String title) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));

        LearningGoal goal = new LearningGoal();
        goal.setUserId(user.getId());
        goal.setTitle(title);
        goal.setCompleted(false);
        goal.setVisible(true);
        goal.setCreatedAt(LocalDateTime.now());
        goal.setUpdatedAt(LocalDateTime.now());

        try {
            goal.checkIncompleteGoalsLimit(learningGoalRepository);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        return learningGoalRepository.save(goal);
    }

    public List<LearningGoal> getGoals(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));
        return learningGoalRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    public LearningGoal updateGoal(String userId, String goalId, String title) {
        LearningGoal goal = learningGoalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Mục tiêu không tồn tại"));

        if (!goal.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Không có quyền cập nhật mục tiêu này");
        }

        goal.setTitle(title);
        goal.setUpdatedAt(LocalDateTime.now());
        return learningGoalRepository.save(goal);
    }

    public void deleteGoal(String userId, String goalId) {
        LearningGoal goal = learningGoalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Mục tiêu không tồn tại"));

        if (!goal.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Không có quyền xóa mục tiêu này");
        }

        learningGoalRepository.delete(goal);
    }

    @Transactional
    public Map<String, Object> toggleGoalCompletion(String userId, String goalId) {
        LearningGoal goal = learningGoalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Mục tiêu không tồn tại"));

        if (!goal.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Không có quyền cập nhật mục tiêu này");
        }

        goal.setCompleted(!goal.isCompleted());
        goal.setCompletedAt(goal.isCompleted() ? LocalDateTime.now() : null);
        goal.setUpdatedAt(LocalDateTime.now());
        learningGoalRepository.save(goal);

        Map<String, Object> response = new HashMap<>();
        response.put("goal", goal);

        if (goal.isCompleted()) {
            // Cập nhật cây học tập và kiểm tra achievements
            LearningTree tree = learningTreeRepository.findByUserId(goal.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy cây học tập"));

            long completedCount = learningGoalRepository.countByUserIdAndIsCompletedTrue(goal.getUserId());
            tree.setCompletedGoalsCount((int) completedCount);
            tree.setLastUpdated(LocalDateTime.now());
            learningTreeRepository.save(tree);
            response.put("tree", tree);

            // Kiểm tra và tạo achievements
            List<Achievement> newAchievements = checkAndCreateAchievements(userId, completedCount);
            response.put("newAchievements", newAchievements);
        }

        return response;
    }

    public LearningGoal toggleGoalVisibility(String userId, String goalId) {
        LearningGoal goal = learningGoalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Mục tiêu không tồn tại"));

        if (!goal.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Không có quyền cập nhật mục tiêu này");
        }

        if (!goal.isCompleted()) {
            throw new IllegalArgumentException("Chỉ có thể ẩn mục tiêu đã hoàn thành");
        }

        goal.setVisible(!goal.isVisible());
        goal.setUpdatedAt(LocalDateTime.now());
        return learningGoalRepository.save(goal);
    }

    private List<Achievement> checkAndCreateAchievements(String userId, long completedCount) {
        List<Achievement> newAchievements = new ArrayList<>();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));

        Achievement.AchievementType[] types = Achievement.AchievementType.values();
        int[] thresholds = { 1, 5, 10, 20, 50, 100 };

        for (int i = 0; i < types.length; i++) {
            if (completedCount >= thresholds[i]) {
                Optional<Achievement> existingAchievement = achievementRepository
                        .findByUserIdAndType(userId, types[i]);

                // Đặc biệt xử lý cho ROOKIE achievement
                if (types[i] == Achievement.AchievementType.ROOKIE && completedCount == 1) {
                    // Luôn tạo mới ROOKIE achievement khi hoàn thành mục tiêu đầu tiên
                    Achievement achievement = new Achievement();
                    achievement.setUserId(user.getId());
                    achievement.setType(types[i]);
                    achievement.setGoalsCompleted((int) completedCount);
                    achievement.setUnlockedAt(LocalDateTime.now());
                    achievement.setDisplayed(false);
                    newAchievements.add(achievementRepository.save(achievement));
                } else if (existingAchievement.isEmpty()) {
                    // Xử lý các achievement khác như bình thường
                    Achievement achievement = new Achievement();
                    achievement.setUserId(user.getId());
                    achievement.setType(types[i]);
                    achievement.setGoalsCompleted((int) completedCount);
                    achievement.setUnlockedAt(LocalDateTime.now());
                    achievement.setDisplayed(false);
                    newAchievements.add(achievementRepository.save(achievement));
                }
            }
        }

        return newAchievements;
    }
}