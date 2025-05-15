package com.example.vibely_backend.service;

import com.example.vibely_backend.entity.Message;
import com.example.vibely_backend.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public Message saveMessage(Message message) {
        return messageRepository.save(message);
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