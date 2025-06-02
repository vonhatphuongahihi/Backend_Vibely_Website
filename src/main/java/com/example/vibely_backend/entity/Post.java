package com.example.vibely_backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "posts")
public class Post {

    @Id
    private String id;

    @Field("user_id")
    private String userId;

    @Field("content")
    private String content;

    @Field("media_url")
    private String mediaUrl;

    @Field("media_type")
    private String mediaType;

    @Field("reactions")
    private List<Reaction> reactions;

    @Field("comments")
    private List<Comment> comments;

    @Field("reaction_stats")
    private ReactionStats reactionStats;

    @Field("reaction_count")
    private int reactionCount;

    @Field("comment_count")
    private int commentCount;

    @Field("share")
    private List<String> share;

    @Field("share_count")
    private int shareCount;

    @Field("created_at")
    private Date createdAt;

    @Field("updated_at")
    private Date updatedAt;

    // Reaction class definition (Inner Class)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Reaction {
        @Field("user_id")
        private String userId;

        @Field("type")
        private String type;

        @Field("created_at")
        private Date createdAt;
    }

    // Comment class definition (Inner Class)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Comment {
        private String id;

        @Field("user_id")
        private String userId;

        @Field("text")
        private String text;

        @Field("created_at")
        private Date createdAt;

        @Field("reactions")
        private List<Reaction> reactions;

        @Field("replies")
        private List<Reply> replies;
    }

    // Reply class definition (Inner Class)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Reply {
        private String id;

        @Field("user_id")
        private String userId;

        @Field("text")
        private String text;

        @Field("created_at")
        private Date createdAt;
    }

    // ReactionStats class definition (Inner Class)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReactionStats {
        @Field("like")
        private int like;

        @Field("love")
        private int love;

        @Field("haha")
        private int haha;

        @Field("wow")
        private int wow;

        @Field("sad")
        private int sad;

        @Field("angry")
        private int angry;
    }
}
