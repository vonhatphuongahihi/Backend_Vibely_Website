package com.example.vibely_backend.repository;

import com.example.vibely_backend.entity.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findByConversationId(String conversationId);
} 