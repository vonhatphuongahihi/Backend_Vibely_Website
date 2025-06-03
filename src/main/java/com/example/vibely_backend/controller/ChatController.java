package com.example.vibely_backend.controller;

import com.example.vibely_backend.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class ChatController {

    @Autowired
    private ChatService chatService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload Map<String, Object> payload) {
        String receiverId = (String) payload.get("receiverId");
        chatService.sendMessage(receiverId, payload);
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload Map<String, Object> payload, SimpMessageHeaderAccessor headerAccessor) {
        String userId = (String) payload.get("userId");
        headerAccessor.getSessionAttributes().put("userId", userId);
        chatService.addOnlineUser(userId);
        
        System.out.println("User added to online list: " + userId);
        System.out.println("Current online users count: " + chatService.getOnlineUsers().size());
        System.out.println("Current online users: " + chatService.getOnlineUsers());
        
        chatService.broadcastOnlineUsers();
    }

    @MessageMapping("/chat.markAsRead")
    public void markAsRead(@Payload Map<String, Object> payload) {
        String receiverId = (String) payload.get("receiverId");
        chatService.sendMessageRead(receiverId, payload);
    }
} 