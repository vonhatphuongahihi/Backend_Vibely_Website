package com.example.vibely_backend.repository;

import com.example.vibely_backend.entity.LearningTree;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface LearningTreeRepository extends MongoRepository<LearningTree, String> {
    Optional<LearningTree> findByUser_Id(String userId);
}
