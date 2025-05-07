package com.example.vibely_backend.repository;

import com.example.vibely_backend.entity.Achievement;
import com.example.vibely_backend.entity.Achievement.AchievementType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AchievementRepository extends MongoRepository<Achievement, String> {

    List<Achievement> findByUserId(String userId);

    Optional<Achievement> findByUserIdAndType(String userId, AchievementType type);
}
