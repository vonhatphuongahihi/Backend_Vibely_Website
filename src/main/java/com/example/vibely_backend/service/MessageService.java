package com.example.vibely_backend.service;

import com.example.vibely_backend.entity.Message;
import com.example.vibely_backend.repository.MessageRepository;
import com.example.vibely_backend.entity.Conversation;
import com.example.vibely_backend.repository.ConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    public Message saveMessage(Message message) {
        Message saved = messageRepository.save(message);
        // Cập nhật lastMessage và lastMessageTime cho Conversation
        if (saved.getConversationId() != null) {
            conversationRepository.findById(saved.getConversationId()).ifPresent(conversation -> {
                conversation.setLastMessage(saved.getContent());
                conversation.setLastMessageTime(saved.getCreatedAt());
                conversationRepository.save(conversation);
            });
        }
        return saved;
    }

    public List<Message> findByConversationId(String conversationId) {
        return messageRepository.findByConversationId(conversationId);
    }

    public void markMessageAsRead(String messageId, String userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tin nhắn"));
        message.setRead(true);
        message.setUpdatedAt(new java.util.Date());
        messageRepository.save(message);
    }
} 