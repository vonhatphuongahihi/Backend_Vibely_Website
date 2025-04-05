package com.example.vibely_backend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Document(collection = "posts")
public class Post {
    @Id
    private String id;
    private String user;
    private String content;
    private String mediaUrl;
    private String mediaType;
    private int reactionCount;
    private int commentCount;
    private int shareCount;
    private Map<String, Integer> reactionStats = new HashMap<>();
    private List<Reaction> reactions = new ArrayList<>();
    private List<Comment> comments = new ArrayList<>();
    private List<String> share = new ArrayList<>();

    @Data
    public static class Reaction {
        private String user;
        private String type;
    }

    @Data
    public static class Comment {
        private String id;
        private String user;
        private String text;
        private List<Reply> replies = new ArrayList<>();
        private List<Reaction> reactions = new ArrayList<>();
    }

    @Data
    public static class Reply {
        private String id;
        private String user;
        private String text;
    }
} 