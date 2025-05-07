package com.example.vibely_backend.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "achievements")
public class Achievement {

    @Id
    private String id;

    @DBRef
    private User user;

    private AchievementType type;
    private int goalsCompleted;
    private LocalDateTime unlockedAt;
    private boolean isDisplayed;

    // Tạo phương thức ảo để lấy thông tin chi tiết về Achievement
    @Field("details")
    public AchievementDetails getDetails() {
        return ACHIEVEMENT_DETAILS.get(this.type);
    }

    // Enum loại Achievement
    public enum AchievementType {
        ROOKIE, // Tân Binh
        TRAINEE, // Tập Sự
        WARRIOR, // Chiến Binh
        ELITE, // Tinh Anh
        MASTER, // Cao Thủ
        GODKING // Thần Vương
    }

    // Thông tin chi tiết về Achievement
    @Getter
    @Setter
    public static class AchievementDetails {
        private String title;
        private String description;
        private String icon;
        private String image;

        public AchievementDetails(String title, String description, String icon, String image) {
            this.title = title;
            this.description = description;
            this.icon = icon;
            this.image = image;
        }
    }

    // Thông tin chi tiết của các Achievement
    public static final Map<AchievementType, AchievementDetails> ACHIEVEMENT_DETAILS = new HashMap<>();

    static {
        ACHIEVEMENT_DETAILS.put(AchievementType.ROOKIE, new AchievementDetails("Tân Binh",
                "Hoàn thành mục tiêu học tập đầu tiên", "🌱", "/study-plant/badges/rookie.png"));
        ACHIEVEMENT_DETAILS.put(AchievementType.TRAINEE, new AchievementDetails("Tập Sự",
                "Hoàn thành 5 mục tiêu học tập", "📚", "/study-plant/badges/trainee.png"));
        ACHIEVEMENT_DETAILS.put(AchievementType.WARRIOR, new AchievementDetails("Chiến Binh",
                "Hoàn thành 10 mục tiêu học tập", "⚔️", "/study-plant/badges/warrior.png"));
        ACHIEVEMENT_DETAILS.put(AchievementType.ELITE, new AchievementDetails("Tinh Anh",
                "Hoàn thành 20 mục tiêu học tập", "💫", "/study-plant/badges/elite.png"));
        ACHIEVEMENT_DETAILS.put(AchievementType.MASTER, new AchievementDetails("Cao Thủ",
                "Hoàn thành 50 mục tiêu học tập", "🔥", "/study-plant/badges/master.png"));
        ACHIEVEMENT_DETAILS.put(AchievementType.GODKING, new AchievementDetails("Thần Vương",
                "Hoàn thành 100 mục tiêu học tập", "👑", "/study-plant/badges/godking.png"));
    }
}
