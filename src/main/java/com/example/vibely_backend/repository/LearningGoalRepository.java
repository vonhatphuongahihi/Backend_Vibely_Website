package com.example.vibely_backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.vibely_backend.entity.LearningGoal;
import com.example.vibely_backend.entity.User;

@Repository
public interface LearningGoalRepository extends MongoRepository<LearningGoal, String> {
    long countByUserAndIsCompletedFalse(User user);
}
