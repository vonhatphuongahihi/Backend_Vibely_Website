package com.example.vibely_backend.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "learningtrees")
public class LearningTree {

    @Id
    private String id;

    @DBRef
    private User user;

    private String treeType;
    private int growthStage;
    private int completedGoalsCount;

    private LocalDateTime lastUpdated;

    // Tạo các hằng số cho tree type
    public static final String CACTUS = "cactus";
    public static final String GREEN_TREE = "green_tree";
    public static final String SUNFLOWER = "sunflower";

    // Quá trình phát triển của cây học tập
    public static final int TAN_BINH = 0;
    public static final int TAP_SU = 1;
    public static final int CHIEN_BINH = 2;
    public static final int TINH_ANH = 3;
    public static final int CAO_THU = 4;
    public static final int THAN_VUONG = 5;

    // Phương thức tính toán growthStage dựa trên completedGoalsCount
    public void updateGrowthStage() {
        if (this.completedGoalsCount >= 100) {
            this.growthStage = THAN_VUONG; // Thần Vương
        } else if (this.completedGoalsCount >= 50) {
            this.growthStage = CAO_THU; // Cao Thủ
        } else if (this.completedGoalsCount >= 20) {
            this.growthStage = TINH_ANH; // Tinh Anh
        } else if (this.completedGoalsCount >= 10) {
            this.growthStage = CHIEN_BINH; // Chiến Binh
        } else if (this.completedGoalsCount >= 5) {
            this.growthStage = TAP_SU; // Tập Sự
        } else {
            this.growthStage = TAN_BINH; // Tân Binh
        }
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void setCompletedGoalsCount(int completedGoalsCount) {
        this.completedGoalsCount = completedGoalsCount;
        updateGrowthStage();
    }
}
