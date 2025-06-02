package com.example.vibely_backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.vibely_backend.entity.LearningGoal;

import java.util.List;

@Repository
public interface LearningGoalRepository extends MongoRepository<LearningGoal, String> {
    List<LearningGoal> findByUserIdOrderByCreatedAtDesc(String userId);

    long countByUserIdAndIsCompletedTrue(String userId);

    long countByUserIdAndIsCompletedFalse(String userId);
}
