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
@Document(collection = "stories")
public class Story {

    @Id
    private String id;

    @Field("user_id")
    private String userId;

    @Field("media_url")
    private String mediaUrl;

    @Field("media_type")
    private String mediaType;

    @Field("reactions")
    private List<Reaction> reactions;

    @Field("reaction_stats")
    private ReactionStats reactionStats;

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

        @Field("created_at")
        private Date createdAt;
    }

    // ReactionStats class definition (Inner Class)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReactionStats {
        @Field("tym")
        private int tym;
    }
}