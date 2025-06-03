package com.example.vibely_backend.repository;

import com.example.vibely_backend.entity.Chatbot;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatbotRepository extends MongoRepository<Chatbot, String> {
    List<Chatbot> findByUserId(String userId);

    List<Chatbot> findByUserIdOrderByCreatedAtDesc(String userId);

    void deleteByUserId(String userId);
}
