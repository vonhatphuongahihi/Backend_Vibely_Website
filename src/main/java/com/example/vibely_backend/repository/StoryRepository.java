package com.example.vibely_backend.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.vibely_backend.entity.Story;

@Repository
public interface StoryRepository extends MongoRepository<Story, String> {
    List<Story> findByUserIdOrderByCreatedAtDesc(String userId);

    void deleteByUserId(String userId);

    List<Story> findByReactionsUserId(String userId);
} 