package com.example.vibely_backend.service;

import com.example.vibely_backend.entity.Message;
import com.example.vibely_backend.repository.MessageRepository;
import com.example.vibely_backend.entity.Conversation;
import com.example.vibely_backend.repository.ConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    public Message saveMessage(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("Tin nhắn không được để trống");
        }

        if (message.getContent() == null || message.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Nội dung tin nhắn không được để trống");
        }

        Message saved = messageRepository.save(message);
        log.info("Đã lưu tin nhắn mới với ID: {}", saved.getId());

        // Cập nhật lastMessage và lastMessageTime cho Conversation
        if (saved.getConversationId() != null) {
            conversationRepository.findById(saved.getConversationId()).ifPresent(conversation -> {
                conversation.setLastMessage(saved.getContent());
                conversation.setLastMessageTime(saved.getCreatedAt());
                conversationRepository.save(conversation);
                log.info("Đã cập nhật thông tin cuộc hội thoại {}", conversation.getId());
            });
        }

        return saved;
    }

    public List<Message> findByConversationId(String conversationId) {
        if (conversationId == null || conversationId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID cuộc hội thoại không hợp lệ");
        }

        List<Message> messages = messageRepository.findByConversationId(conversationId);
        log.info("Đã lấy {} tin nhắn từ cuộc hội thoại {}", messages.size(), conversationId);
        return messages;
    }

    public void markMessageAsRead(String messageId, String userId) {
        if (messageId == null || messageId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID tin nhắn không hợp lệ");
        }

        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID người dùng không hợp lệ");
        }

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> {
                    log.error("Không tìm thấy tin nhắn với ID: {}", messageId);
                    return new RuntimeException("Không tìm thấy tin nhắn");
                });

        message.setRead(true);
        message.setUpdatedAt(new java.util.Date());
        messageRepository.save(message);
        log.info("Đã đánh dấu tin nhắn {} là đã đọc bởi người dùng {}", messageId, userId);
    }
}