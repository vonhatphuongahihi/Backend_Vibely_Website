package com.example.vibely_backend.service;

import com.example.vibely_backend.entity.Conversation;
import com.example.vibely_backend.repository.ConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;

    public Conversation createOrGetConversation(String senderId, String receiverId) {
        // Kiểm tra xem đã có cuộc trò chuyện giữa 2 người chưa
        Conversation existingConversation = conversationRepository
                .findConversationByTwoMembers(senderId, receiverId);

        if (existingConversation != null) {
            return existingConversation;
        }

        // Nếu chưa có thì tạo mới
        Conversation newConversation = new Conversation();
        newConversation.setMembers(Arrays.asList(senderId, receiverId));

        return conversationRepository.save(newConversation);
    }

    public List<Conversation> getUserConversations(String userId) {
        return conversationRepository.findByMembersContaining(userId);
    }

    public Conversation getConversationBetweenUsers(String firstUserId, String secondUserId) {
        return conversationRepository.findConversationByTwoMembers(firstUserId, secondUserId);
    }

    public void changeNickname(String conversationId, String userId, String nickname) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cuộc trò chuyện"));

        conversation.getNicknames().put(userId, nickname);
        conversationRepository.save(conversation);
    }

    public void deleteConversation(String conversationId) {
        conversationRepository.deleteById(conversationId);
    }

    public String getNickname(String conversationId, String userId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cuộc trò chuyện"));

        return conversation.getNicknames().get(userId);
    }

    public void changeColor(String conversationId, String color) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cuộc trò chuyện"));

        conversation.setColor(color);
        conversationRepository.save(conversation);
    }
} 