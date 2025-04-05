package com.example.vibely_backend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Document(collection = "stories")
public class Story {
    @Id
    private String id;
    private String user;
    private String mediaUrl;
    private String mediaType;
    private List<Reaction> reactions = new ArrayList<>();
    private Map<String, Integer> reactionStats = new HashMap<>();

    @Data
    public static class Reaction {
        private String user;
        private String type;
    }
} 