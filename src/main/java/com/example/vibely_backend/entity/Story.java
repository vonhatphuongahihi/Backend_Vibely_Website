package com.example.vibely_backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "stories")
public class Story {

    @Id
    private String id;

    @DBRef
    private User user;

    private String mediaUrl;
    private String mediaType;

    private List<Reaction> reactions;
    private ReactionStats reactionStats;

    private Date createdAt;
    private Date updatedAt;

    // Reaction class definition (Inner Class)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Reaction {
        @DBRef
        private User user;
        private Date createdAt;
    }

    // ReactionStats class definition (Inner Class)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReactionStats {
        private int tym;
    }
} 