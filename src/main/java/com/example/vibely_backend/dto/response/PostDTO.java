package com.example.vibely_backend.dto.response;

import com.example.vibely_backend.entity.Post;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {
    private String id;
    private String content;
    private String mediaUrl;
    private String mediaType;
    private int reactionCount;
    private int commentCount;
    private int shareCount;
    private Date createdAt;
    private Date updatedAt;
    private UserMiniDTO user;
    private List<Reaction> reactions;
    private List<Comment> comments;
    private ReactionStats reactionStats;

    // Constructor to handle both Post and UserMiniDTO
    public PostDTO(Post post, UserMiniDTO userDTO) {
        this.id = post.getId();
        this.content = post.getContent();
        this.mediaUrl = post.getMediaUrl();
        this.mediaType = post.getMediaType();
        this.reactionCount = post.getReactionCount();
        this.commentCount = post.getCommentCount();
        this.shareCount = post.getShareCount();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
        this.reactionStats = new ReactionStats(
            post.getReactionStats() != null ? post.getReactionStats().getLike() : 0,
            post.getReactionStats() != null ? post.getReactionStats().getLove() : 0,
            post.getReactionStats() != null ? post.getReactionStats().getHaha() : 0,
            post.getReactionStats() != null ? post.getReactionStats().getWow() : 0,
            post.getReactionStats() != null ? post.getReactionStats().getSad() : 0,
            post.getReactionStats() != null ? post.getReactionStats().getAngry() : 0
        );

        // Assign userDTO to the user field
        this.user = userDTO;

        // Map reactions if present
        if (post.getReactions() != null) {
            this.reactions = post.getReactions().stream().map(r -> new Reaction(
                    new UserMiniDTO(r.getUser()), // Assuming Reaction has a User reference
                    r.getType(),
                    r.getCreatedAt()
            )).collect(Collectors.toList());
        }

        // Map comments if present
        if (post.getComments() != null) {
            this.comments = post.getComments().stream().map(c -> new Comment(
                    c.getId(),
                    new UserMiniDTO(c.getUser()), // Assuming Comment has a User reference
                    c.getText(),
                    c.getCreatedAt(),
                    c.getReactions() != null ? c.getReactions().stream().map(r -> new Reaction(
                            new UserMiniDTO(r.getUser()),
                            r.getType(),
                            r.getCreatedAt()
                    )).collect(Collectors.toList()) : null,
                    c.getReplies() != null ? c.getReplies().stream().map(reply -> new Reply(
                            reply.getId(),
                            new UserMiniDTO(reply.getUser()), // Assuming Reply has a User reference
                            reply.getText(),
                            reply.getCreatedAt()
                    )).collect(Collectors.toList()) : null
            )).collect(Collectors.toList());
        }
    }

    // Nested Reaction class
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Reaction {
        private UserMiniDTO user;
        private String type;
        private Date createdAt;
    }

    // Nested Comment class
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Comment {
        private String id;
        private UserMiniDTO user;
        private String text;
        private Date createdAt;
        private List<Reaction> reactions;
        private List<Reply> replies;
    }

    // Nested Reply class
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Reply {
        private String id;
        private UserMiniDTO user;
        private String text;
        private Date createdAt;
    }

    // Nested ReactionStats class
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
