package com.example.vibely_backend.controller;

import com.example.vibely_backend.entity.Chatbot;
import com.example.vibely_backend.service.ChatbotService;
import com.example.vibely_backend.service.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chatbot")
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    @Autowired
    private JWTService jwtService;

    private String getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }
        return jwtService.extractUserIdFromToken(authentication.getCredentials().toString());
    }

    @PostMapping
    public ResponseEntity<?> createChat(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        String answer = request.get("answer");

        if (text == null || text.isEmpty()) {
            return ResponseEntity.badRequest().body("Bạn cần nhập nội dung câu hỏi");
        }

        Chatbot chat = chatbotService.createChat(getUserId(), text, answer);
        return ResponseEntity.ok(chat);
    }

    @GetMapping
    public ResponseEntity<?> getChats() {
        List<Chatbot> chats = chatbotService.getChats(getUserId());
        return ResponseEntity.ok(chats);
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<?> getChatItem(@PathVariable String chatId) {
        Chatbot chat = chatbotService.getChatItem(getUserId(), chatId);
        return ResponseEntity.ok(chat);
    }

    @PutMapping("/{chatId}")
    public ResponseEntity<?> putQuestion(@PathVariable String chatId,
            @RequestBody Map<String, String> request) {
        String question = request.get("question");
        String answer = request.get("answer");
        String img = request.get("img");

        Chatbot chat = chatbotService.putQuestion(getUserId(), chatId, question, answer, img);
        return ResponseEntity.ok(chat);
    }

    @DeleteMapping("/history")
    public ResponseEntity<?> deleteChatHistory() {
        chatbotService.deleteChatHistory(getUserId());
        return ResponseEntity.ok(Map.of("message", "Xóa lịch sử chat thành công"));
    }

    @GetMapping("/history")
    public ResponseEntity<?> getChatHistory() {
        List<Chatbot> history = chatbotService.getChatHistory(getUserId());
        return ResponseEntity.ok(Map.of(
                "history", history,
                "message", "Lấy lịch sử chat thành công"));
    }

    @PostMapping("/handleMessage")
    public ResponseEntity<?> handleMessage(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        if (message == null || message.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Tin nhắn không được để trống");
        }

        String response = chatbotService.handleMessage(getUserId(), message);
        return ResponseEntity.ok(Map.of(
                "message", response,
                "timestamp", System.currentTimeMillis()));
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChatbotResponse(@RequestParam String text,
            @RequestParam(required = false) String chatId) {
        SseEmitter emitter = chatbotService.createEmitter();

        // Xử lý bất đồng bộ để không block request
        new Thread(() -> {
            try {
                String response = chatbotService.handleMessage(getUserId(), text);
                emitter.send(SseEmitter.event()
                        .name("message")
                        .data(response));
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }
}