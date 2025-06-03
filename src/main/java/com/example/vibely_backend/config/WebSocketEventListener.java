package com.example.vibely_backend.config;

import com.example.vibely_backend.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    @Autowired
    private ChatService chatService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        // Log khi có connection mới
        System.out.println("Received a new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        
        String userId = (String) headerAccessor.getSessionAttributes().get("userId");
        if (userId != null) {
            System.out.println("User Disconnected: " + userId);
            
            // Xóa user khỏi danh sách online
            chatService.removeOnlineUser(userId);
            
            // Log số lượng user online hiện tại
            System.out.println("Current online users count: " + chatService.getOnlineUsers().size());
            System.out.println("Current online users: " + chatService.getOnlineUsers());
            
            // Broadcast danh sách online mới cho tất cả users
            chatService.broadcastOnlineUsers();
        } else {
            System.out.println("User disconnected but no userId found in session attributes");
        }
    }
} 