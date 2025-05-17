package com.example.vibely_backend.controller;

import com.example.vibely_backend.entity.Message;
import com.example.vibely_backend.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping
    public ResponseEntity<?> createMessage(@RequestBody Message message) {
        try {
            Message savedMessage = messageService.saveMessage(message);
            return ResponseEntity.ok(savedMessage);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<?> getMessagesByConversationId(@PathVariable String conversationId) {
        try {
            List<Message> messages = messageService.findByConversationId(conversationId);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/read")
    public ResponseEntity<?> markMessageAsRead(@RequestBody java.util.Map<String, String> request) {
        String messageId = request.get("messageId");
        String userId = request.get("userId");
        try {
            messageService.markMessageAsRead(messageId, userId);
            return ResponseEntity.ok("Đã đánh dấu đã đọc");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
