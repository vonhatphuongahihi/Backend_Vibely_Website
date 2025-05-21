package com.example.vibely_backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.example.vibely_backend.entity.ChatbotTrainingData;
import java.util.List;

@Repository
public interface ChatbotTrainingDataRepository extends MongoRepository<ChatbotTrainingData, String> {
    List<ChatbotTrainingData> findByCategory(String category);

    List<ChatbotTrainingData> findByKeywordsContaining(String keyword);
}
