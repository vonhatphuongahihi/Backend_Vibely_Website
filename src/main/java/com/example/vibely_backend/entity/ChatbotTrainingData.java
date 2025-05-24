package com.example.vibely_backend.entity;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chatbot_training_data")
public class ChatbotTrainingData {
    @Id
    private String id;

    private String question;
    private String answer;
    private String category; // Phân loại câu hỏi/trả lời
    private List<String> keywords; // Từ khóa liên quan
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
