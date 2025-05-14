package com.example.vibely_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendMessage(String receiverId, Object message) {
        messagingTemplate.convertAndSendToUser(
            receiverId,
            "/queue/messages",
            message
        );
    }

    public void sendMessageRead(String receiverId, Object message) {
        messagingTemplate.convertAndSendToUser(
            receiverId,
            "/queue/read",
            message
        );
    }
} 