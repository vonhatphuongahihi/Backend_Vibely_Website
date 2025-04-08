package com.example.vibely_backend.repository;

import com.example.vibely_backend.entity.Story;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoryRepository extends MongoRepository<Story, String> {
    List<Story> findByUserIdOrderByCreatedAtDesc(String userId);
} 