package com.example.vibely_backend.service;

import com.example.vibely_backend.model.Story;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface StoryService extends MongoRepository<Story, String> {
    List<Story> findAllWithDetails();
    Story reactStory(String storyId, String userId);
} 