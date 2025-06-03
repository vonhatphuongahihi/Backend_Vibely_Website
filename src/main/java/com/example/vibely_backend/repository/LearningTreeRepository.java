package com.example.vibely_backend.repository;

import com.example.vibely_backend.entity.LearningTree;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LearningTreeRepository extends MongoRepository<LearningTree, String> {
    Optional<LearningTree> findByUserId(String userId);
}
