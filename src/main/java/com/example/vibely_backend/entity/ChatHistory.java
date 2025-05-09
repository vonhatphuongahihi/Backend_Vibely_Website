package com.example.vibely_backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatHistory {
    private String role;

    @Field("parts")
    private List<ChatPart> parts;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatPart {
        private String text;
    }
}