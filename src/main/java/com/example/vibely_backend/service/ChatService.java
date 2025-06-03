package com.example.vibely_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
public class ChatService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Danh sách userId đang online
    private static final Set<String> onlineUsers = ConcurrentHashMap.newKeySet();

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

    // Thêm user vào danh sách online
    public void addOnlineUser(String userId) {
        onlineUsers.add(userId);
    }

    // Xóa user khỏi danh sách online
    public void removeOnlineUser(String userId) {
        onlineUsers.remove(userId);
    }

    // Lấy danh sách user đang online
    public Set<String> getOnlineUsers() {
        return onlineUsers;
    }

    // Gửi danh sách online cho từng user
    public void broadcastOnlineUsers() {
        System.out.println("Broadcasting online users to " + onlineUsers.size() + " users");
        for (String userId : onlineUsers) {
            messagingTemplate.convertAndSendToUser(
                userId,
                "/queue/users",
                onlineUsers.stream().map(id -> Map.of("userId", id)).toList()
            );
        }
        System.out.println("Broadcast completed");
    }
} 