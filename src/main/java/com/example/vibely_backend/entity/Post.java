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
@Document(collection = "posts")
public class Post {

    @Id
    private String id;

    @DBRef
    private User user;

    private String content;
    private String mediaUrl;
    private String mediaType;

    private List<Reaction> reactions;

    private List<Comment> comments;

    private ReactionStats reactionStats;

    private int reactionCount;
    private int commentCount;

    private List<User> share;
    private int shareCount;

    private Date createdAt;
    private Date updatedAt;

    // Reaction class definition (Inner Class)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Reaction {
        @DBRef
        private User user;

        private String type;
        private Date createdAt;
    }

    // Comment class definition (Inner Class)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Comment {
        private String id;
        @DBRef
        private User user;

        private String text;
        private Date createdAt;

        private List<Reaction> reactions;

        private List<Reply> replies;
    }

    // Reply class definition (Inner Class)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Reply {
        private String id;
        @DBRef
        private User user;

        private String text;
        private Date createdAt;
    }

    // ReactionStats class definition (Inner Class)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReactionStats {
        private int like;
        private int love;
        private int haha;
        private int wow;
        private int sad;
        private int angry;
    }
}
