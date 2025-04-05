package com.example.vibely_backend.service;

import com.example.vibely_backend.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PostService extends MongoRepository<Post, String> {
    List<Post> findAllWithDetails();
    List<Post> findByUserId(String userId);
    Post findByIdWithDetails(String id);
    Post reactPost(String postId, String userId, String type);
    Post addComment(String postId, String userId, String text);
    Post addReply(String postId, String commentId, String userId, String replyText);
    Post sharePost(String postId, String userId);
    Post deletePost(String postId);
    Post deleteComment(String postId, String commentId);
    Post deleteReply(String postId, String commentId, String replyId);
    Post likeComment(String postId, String commentId, String userId);
} 