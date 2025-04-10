package com.example.vibely_backend.repository;

import com.example.vibely_backend.entity.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepository extends MongoRepository<Conversation, String> {
    List<Conversation> findByMembersContaining(String userId);
    
    @Query("{ 'members': { $all: [?0, ?1], $size: 2 } }")
    Conversation findConversationByTwoMembers(String firstUserId, String secondUserId);
} 