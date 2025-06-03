package com.example.vibely_backend.dto.response;

import com.example.vibely_backend.entity.Story;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoryDTO {
    private String id;
    private String mediaUrl;
    private String mediaType;
    private Date createdAt;
    private Date updatedAt;
    private UserMiniDTO user;
    private List<Reaction> reactions;
    private ReactionStats reactionStats;

    // Constructor to handle both Story and UserMiniDTO
    public StoryDTO(Story story, UserMiniDTO userDTO) {
        this.id = story.getId();
        this.mediaUrl = story.getMediaUrl();
        this.mediaType = story.getMediaType();
        this.createdAt = story.getCreatedAt();
        this.updatedAt = story.getUpdatedAt();

        // Assign userDTO to the user field
        this.user = userDTO;

        // Map reactions if present
        if (story.getReactions() != null) {
            this.reactions = story.getReactions().stream()
                    .map(r -> new Reaction(
                            r.getUserId(),
                            r.getCreatedAt()))
                    .collect(Collectors.toList());
        }

        // Map reactionStats if present
        if (story.getReactionStats() != null) {
            this.reactionStats = new ReactionStats(story.getReactionStats().getTym());
        } else {
            this.reactionStats = new ReactionStats(0);
        }
    }

    // Nested Reaction class
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Reaction {
        private String userId; // Changed from UserMiniDTO to String
        private Date createdAt;
    }

    // Nested ReactionStats class
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReactionStats {
        private int tym;
    }
}