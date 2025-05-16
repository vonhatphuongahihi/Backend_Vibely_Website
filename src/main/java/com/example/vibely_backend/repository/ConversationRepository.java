package com.example.vibely_backend.repository;

import com.example.vibely_backend.entity.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepository extends MongoRepository<Conversation, String> {
    List<Conversation> findByMembersContaining(String userId);
    
    @Query(value = "{ 'members': { $all: [?0, ?1], $size: 2 } }")
    List<Conversation> findAllConversationsByTwoMembers(String firstUserId, String secondUserId);

    default Conversation findConversationByTwoMembers(String firstUserId, String secondUserId) {
        List<Conversation> conversations = findAllConversationsByTwoMembers(firstUserId, secondUserId);
        return conversations.isEmpty() ? null : conversations.get(0);
    }
} 