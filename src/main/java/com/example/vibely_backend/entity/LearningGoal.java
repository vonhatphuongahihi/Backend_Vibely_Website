package com.example.vibely_backend.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.example.vibely_backend.repository.LearningGoalRepository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "learninggoals")
public class LearningGoal {

    @Id
    private String id;

    private String userId;

    private String title;

    @Field("isCompleted")
    private boolean isCompleted;

    @Field("completed_at")
    private LocalDateTime completedAt;

    @Field("isVisible")
    private boolean isVisible;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    // Đảm bảo khi lưu mới, kiểm tra số lượng mục tiêu chưa hoàn thành của người
    // dùng
    public void checkIncompleteGoalsLimit(LearningGoalRepository learningGoalRepository) throws Exception {
        if (this.isNew()) { // Chỉ kiểm tra khi là đối tượng mới
            long incompleteGoalsCount = learningGoalRepository.countByUserIdAndIsCompletedFalse(this.userId);

            if (incompleteGoalsCount >= 5) {
                throw new Exception("Bạn đã có 5 mục tiêu chưa hoàn thành. Không thể tạo thêm.");
            }
        }
    }

    // Phương thức kiểm tra xem đối tượng này có phải là đối tượng mới hay không
    public boolean isNew() {
        return this.id == null;
    }
}
