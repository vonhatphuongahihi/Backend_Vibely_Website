package com.example.vibely_backend.controller;

import com.example.vibely_backend.entity.Conversation;
import com.example.vibely_backend.entity.User;
import com.example.vibely_backend.repository.UserRepository;
import com.example.vibely_backend.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/conversations")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> createConversation(@RequestBody Map<String, String> request) {
        try {
            String senderId = request.get("senderId");
            String receiverId = request.get("receiverId");

            if (senderId == null || receiverId == null) {
                return ResponseEntity.badRequest().body("Thiếu senderId hoặc receiverId");
            }

            Conversation conversation = conversationService.createOrGetConversation(senderId, receiverId);
            return ResponseEntity.ok(conversation);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi server: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserConversations(@PathVariable String userId) {
        try {
            if (userId == null) {
                return ResponseEntity.badRequest().body("Thiếu userId");
            }
            List<Conversation> conversations = conversationService.getUserConversations(userId);
            List<Map<String, Object>> result = new ArrayList<>();
            for (Conversation conv : conversations) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", conv.getId());
                map.put("members", conv.getMembers());
                map.put("lastMessage", conv.getLastMessage());
                map.put("lastMessageTime", conv.getLastMessageTime());
                map.put("color", conv.getColor());
                // Lấy thông tin user cho từng member
                List<Map<String, Object>> membersData = conv.getMembers().stream().map(memberId -> {
                    User user = userRepository.findById(memberId).orElse(null);
                    if (user == null) return null;
                    Map<String, Object> u = new HashMap<>();
                    u.put("id", user.getId());
                    u.put("username", user.getUsername());
                    u.put("profilePicture", user.getProfilePicture());
                    return u;
                }).filter(java.util.Objects::nonNull).collect(Collectors.toList());
                map.put("membersData", membersData);
                result.add(map);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi server: " + e.getMessage());
        }
    }

    @GetMapping("/between/{firstUserId}/{secondUserId}")
    public ResponseEntity<?> getConversationBetweenUsers(
            @PathVariable String firstUserId,
            @PathVariable String secondUserId) {
        try {
            if (firstUserId == null || secondUserId == null) {
                return ResponseEntity.badRequest().body("Thiếu userId");
            }

            Conversation conversation = conversationService.getConversationBetweenUsers(firstUserId, secondUserId);
            return ResponseEntity.ok(conversation);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi server: " + e.getMessage());
        }
    }

    @PutMapping("/nickname")
    public ResponseEntity<?> changeNickname(@RequestBody Map<String, String> request) {
        try {
            String conversationId = request.get("conversationId");
            String userId = request.get("userId");
            String nickname = request.get("nickname");

            conversationService.changeNickname(conversationId, userId, nickname);
            return ResponseEntity.ok("Đặt biệt danh thành công");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi server: " + e.getMessage());
        }
    }

    @DeleteMapping("/{conversationId}")
    public ResponseEntity<?> deleteConversation(@PathVariable String conversationId) {
        try {
            conversationService.deleteConversation(conversationId);
            return ResponseEntity.ok("Xóa cuộc trò chuyện thành công");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi server: " + e.getMessage());
        }
    }

    @GetMapping("/{conversationId}/nickname/{userId}")
    public ResponseEntity<?> getNickname(
            @PathVariable String conversationId,
            @PathVariable String userId) {
        try {
            String nickname = conversationService.getNickname(conversationId, userId);
            return ResponseEntity.ok(Map.of("nickname", nickname));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi server: " + e.getMessage());
        }
    }

    @PutMapping("/color")
    public ResponseEntity<?> changeColor(@RequestBody Map<String, String> request) {
        try {
            String conversationId = request.get("conversationId");
            String color = request.get("color");

            conversationService.changeColor(conversationId, color);
            return ResponseEntity.ok("Đổi màu đoạn chat thành công");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi server: " + e.getMessage());
        }
    }
}
