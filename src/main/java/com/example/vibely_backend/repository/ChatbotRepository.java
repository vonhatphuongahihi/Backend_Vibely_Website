package com.example.vibely_backend.repository;

import com.example.vibely_backend.entity.Chatbot;
import com.example.vibely_backend.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatbotRepository extends MongoRepository<Chatbot, String> {
    List<Chatbot> findByUser(User user);

    List<Chatbot> findByUserOrderByCreatedAtDesc(User user);

    void deleteByUser(User user);
}
