package com.example.vibely_backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.vibely_backend.dto.response.PostDTO;
import com.example.vibely_backend.dto.response.UserMiniDTO;
import com.example.vibely_backend.entity.Post;
import com.example.vibely_backend.entity.User;
import com.example.vibely_backend.repository.PostRepository;
import com.example.vibely_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public List<PostDTO> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream().map(post -> {
            User postUser = userRepository.findById(post.getUserId()).orElse(null);
            UserMiniDTO userDTO = postUser != null ? new UserMiniDTO(postUser) : null;
            
            // Tạo PostDTO với thông tin user đầy đủ cho reactions
            PostDTO postDTO = new PostDTO();
            postDTO.setId(post.getId());
            postDTO.setContent(post.getContent());
            postDTO.setMediaUrl(post.getMediaUrl());
            postDTO.setMediaType(post.getMediaType());
            postDTO.setReactionCount(post.getReactionCount());
            postDTO.setCommentCount(post.getCommentCount());
            postDTO.setShareCount(post.getShareCount());
            postDTO.setCreatedAt(post.getCreatedAt());
            postDTO.setUpdatedAt(post.getUpdatedAt());
            postDTO.setUser(userDTO);
            
            // Set reactionStats
            if (post.getReactionStats() != null) {
                PostDTO.ReactionStats reactionStats = new PostDTO.ReactionStats();
                reactionStats.setLike(post.getReactionStats().getLike());
                reactionStats.setLove(post.getReactionStats().getLove());
                reactionStats.setHaha(post.getReactionStats().getHaha());
                reactionStats.setWow(post.getReactionStats().getWow());
                reactionStats.setSad(post.getReactionStats().getSad());
                reactionStats.setAngry(post.getReactionStats().getAngry());
                postDTO.setReactionStats(reactionStats);
            }
            
            // Populate reactions với thông tin user đầy đủ
            if (post.getReactions() != null) {
                List<PostDTO.Reaction> reactions = post.getReactions().stream().map(reaction -> {
                    User reactionUser = userRepository.findById(reaction.getUserId()).orElse(null);
                    UserMiniDTO reactionUserDTO = reactionUser != null ? new UserMiniDTO(reactionUser) : new UserMiniDTO(reaction.getUserId());
                    return new PostDTO.Reaction(reactionUserDTO, reaction.getType(), reaction.getCreatedAt());
                }).collect(Collectors.toList());
                postDTO.setReactions(reactions);
            }
            
            // Populate comments với thông tin user đầy đủ
            if (post.getComments() != null) {
                List<PostDTO.Comment> comments = post.getComments().stream().map(comment -> {
                    User commentUser = userRepository.findById(comment.getUserId()).orElse(null);
                    UserMiniDTO commentUserDTO = commentUser != null ? new UserMiniDTO(commentUser) : new UserMiniDTO(comment.getUserId());
                    
                    // Populate comment reactions
                    List<PostDTO.Reaction> commentReactions = null;
                    if (comment.getReactions() != null) {
                        commentReactions = comment.getReactions().stream().map(reaction -> {
                            User reactUser = userRepository.findById(reaction.getUserId()).orElse(null);
                            UserMiniDTO reactUserDTO = reactUser != null ? new UserMiniDTO(reactUser) : new UserMiniDTO(reaction.getUserId());
                            return new PostDTO.Reaction(reactUserDTO, reaction.getType(), reaction.getCreatedAt());
                        }).collect(Collectors.toList());
                    }
                    
                    // Populate replies
                    List<PostDTO.Reply> replies = null;
                    if (comment.getReplies() != null) {
                        replies = comment.getReplies().stream().map(reply -> {
                            User replyUser = userRepository.findById(reply.getUserId()).orElse(null);
                            UserMiniDTO replyUserDTO = replyUser != null ? new UserMiniDTO(replyUser) : new UserMiniDTO(reply.getUserId());
                            return new PostDTO.Reply(reply.getId(), replyUserDTO, reply.getText(), reply.getCreatedAt());
                        }).collect(Collectors.toList());
                    }
                    
                    return new PostDTO.Comment(comment.getId(), commentUserDTO, comment.getText(), comment.getCreatedAt(), commentReactions, replies);
                }).collect(Collectors.toList());
                postDTO.setComments(comments);
            }
            
            return postDTO;
        }).collect(Collectors.toList());
    }

    public PostDTO getPostById(String postId) {
        return postRepository.findById(postId).map(post -> {
            User postUser = userRepository.findById(post.getUserId()).orElse(null);
            UserMiniDTO userDTO = postUser != null ? new UserMiniDTO(postUser) : null;
            
            // Tạo PostDTO với thông tin user đầy đủ cho reactions
            PostDTO postDTO = new PostDTO();
            postDTO.setId(post.getId());
            postDTO.setContent(post.getContent());
            postDTO.setMediaUrl(post.getMediaUrl());
            postDTO.setMediaType(post.getMediaType());
            postDTO.setReactionCount(post.getReactionCount());
            postDTO.setCommentCount(post.getCommentCount());
            postDTO.setShareCount(post.getShareCount());
            postDTO.setCreatedAt(post.getCreatedAt());
            postDTO.setUpdatedAt(post.getUpdatedAt());
            postDTO.setUser(userDTO);
            
            // Set reactionStats
            if (post.getReactionStats() != null) {
                PostDTO.ReactionStats reactionStats = new PostDTO.ReactionStats();
                reactionStats.setLike(post.getReactionStats().getLike());
                reactionStats.setLove(post.getReactionStats().getLove());
                reactionStats.setHaha(post.getReactionStats().getHaha());
                reactionStats.setWow(post.getReactionStats().getWow());
                reactionStats.setSad(post.getReactionStats().getSad());
                reactionStats.setAngry(post.getReactionStats().getAngry());
                postDTO.setReactionStats(reactionStats);
            }
            
            // Populate reactions với thông tin user đầy đủ
            if (post.getReactions() != null) {
                List<PostDTO.Reaction> reactions = post.getReactions().stream().map(reaction -> {
                    User reactionUser = userRepository.findById(reaction.getUserId()).orElse(null);
                    UserMiniDTO reactionUserDTO = reactionUser != null ? new UserMiniDTO(reactionUser) : new UserMiniDTO(reaction.getUserId());
                    return new PostDTO.Reaction(reactionUserDTO, reaction.getType(), reaction.getCreatedAt());
                }).collect(Collectors.toList());
                postDTO.setReactions(reactions);
            }
            
            // Populate comments với thông tin user đầy đủ
            if (post.getComments() != null) {
                List<PostDTO.Comment> comments = post.getComments().stream().map(comment -> {
                    User commentUser = userRepository.findById(comment.getUserId()).orElse(null);
                    UserMiniDTO commentUserDTO = commentUser != null ? new UserMiniDTO(commentUser) : new UserMiniDTO(comment.getUserId());
                    
                    // Populate comment reactions
                    List<PostDTO.Reaction> commentReactions = null;
                    if (comment.getReactions() != null) {
                        commentReactions = comment.getReactions().stream().map(reaction -> {
                            User reactUser = userRepository.findById(reaction.getUserId()).orElse(null);
                            UserMiniDTO reactUserDTO = reactUser != null ? new UserMiniDTO(reactUser) : new UserMiniDTO(reaction.getUserId());
                            return new PostDTO.Reaction(reactUserDTO, reaction.getType(), reaction.getCreatedAt());
                        }).collect(Collectors.toList());
                    }
                    
                    // Populate replies
                    List<PostDTO.Reply> replies = null;
                    if (comment.getReplies() != null) {
                        replies = comment.getReplies().stream().map(reply -> {
                            User replyUser = userRepository.findById(reply.getUserId()).orElse(null);
                            UserMiniDTO replyUserDTO = replyUser != null ? new UserMiniDTO(replyUser) : new UserMiniDTO(reply.getUserId());
                            return new PostDTO.Reply(reply.getId(), replyUserDTO, reply.getText(), reply.getCreatedAt());
                        }).collect(Collectors.toList());
                    }
                    
                    return new PostDTO.Comment(comment.getId(), commentUserDTO, comment.getText(), comment.getCreatedAt(), commentReactions, replies);
                }).collect(Collectors.toList());
                postDTO.setComments(comments);
            }
            
            return postDTO;
        }).orElse(null);
    }

    public List<PostDTO> getPostsByUserId(String userId) {
        List<Post> posts = postRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return posts.stream().map(post -> {
            User postUser = userRepository.findById(post.getUserId()).orElse(null);
            UserMiniDTO userDTO = postUser != null ? new UserMiniDTO(postUser) : null;
            
            // Tạo PostDTO với thông tin user đầy đủ cho reactions
            PostDTO postDTO = new PostDTO();
            postDTO.setId(post.getId());
            postDTO.setContent(post.getContent());
            postDTO.setMediaUrl(post.getMediaUrl());
            postDTO.setMediaType(post.getMediaType());
            postDTO.setReactionCount(post.getReactionCount());
            postDTO.setCommentCount(post.getCommentCount());
            postDTO.setShareCount(post.getShareCount());
            postDTO.setCreatedAt(post.getCreatedAt());
            postDTO.setUpdatedAt(post.getUpdatedAt());
            postDTO.setUser(userDTO);
            
            // Set reactionStats
            if (post.getReactionStats() != null) {
                PostDTO.ReactionStats reactionStats = new PostDTO.ReactionStats();
                reactionStats.setLike(post.getReactionStats().getLike());
                reactionStats.setLove(post.getReactionStats().getLove());
                reactionStats.setHaha(post.getReactionStats().getHaha());
                reactionStats.setWow(post.getReactionStats().getWow());
                reactionStats.setSad(post.getReactionStats().getSad());
                reactionStats.setAngry(post.getReactionStats().getAngry());
                postDTO.setReactionStats(reactionStats);
            }
            
            // Populate reactions với thông tin user đầy đủ
            if (post.getReactions() != null) {
                List<PostDTO.Reaction> reactions = post.getReactions().stream().map(reaction -> {
                    User reactionUser = userRepository.findById(reaction.getUserId()).orElse(null);
                    UserMiniDTO reactionUserDTO = reactionUser != null ? new UserMiniDTO(reactionUser) : new UserMiniDTO(reaction.getUserId());
                    return new PostDTO.Reaction(reactionUserDTO, reaction.getType(), reaction.getCreatedAt());
                }).collect(Collectors.toList());
                postDTO.setReactions(reactions);
            }
            
            // Populate comments với thông tin user đầy đủ
            if (post.getComments() != null) {
                List<PostDTO.Comment> comments = post.getComments().stream().map(comment -> {
                    User commentUser = userRepository.findById(comment.getUserId()).orElse(null);
                    UserMiniDTO commentUserDTO = commentUser != null ? new UserMiniDTO(commentUser) : new UserMiniDTO(comment.getUserId());
                    
                    // Populate comment reactions
                    List<PostDTO.Reaction> commentReactions = null;
                    if (comment.getReactions() != null) {
                        commentReactions = comment.getReactions().stream().map(reaction -> {
                            User reactUser = userRepository.findById(reaction.getUserId()).orElse(null);
                            UserMiniDTO reactUserDTO = reactUser != null ? new UserMiniDTO(reactUser) : new UserMiniDTO(reaction.getUserId());
                            return new PostDTO.Reaction(reactUserDTO, reaction.getType(), reaction.getCreatedAt());
                        }).collect(Collectors.toList());
                    }
                    
                    // Populate replies
                    List<PostDTO.Reply> replies = null;
                    if (comment.getReplies() != null) {
                        replies = comment.getReplies().stream().map(reply -> {
                            User replyUser = userRepository.findById(reply.getUserId()).orElse(null);
                            UserMiniDTO replyUserDTO = replyUser != null ? new UserMiniDTO(replyUser) : new UserMiniDTO(reply.getUserId());
                            return new PostDTO.Reply(reply.getId(), replyUserDTO, reply.getText(), reply.getCreatedAt());
                        }).collect(Collectors.toList());
                    }
                    
                    return new PostDTO.Comment(comment.getId(), commentUserDTO, comment.getText(), comment.getCreatedAt(), commentReactions, replies);
                }).collect(Collectors.toList());
                postDTO.setComments(comments);
            }
            
            return postDTO;
        }).collect(Collectors.toList());
    }
}
