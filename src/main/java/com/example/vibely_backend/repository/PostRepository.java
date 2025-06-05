package com.example.vibely_backend.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.vibely_backend.entity.Post;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    List<Post> findByUserIdOrderByCreatedAtDesc(String userId);

    void deleteByUserId(String userId);

    @Query("{ $or: ["
            + "{ 'comments.user_id': ?0 },"
            + "{ 'comments.replies.user_id': ?0 },"
            + "{ 'reactions.user_id': ?0 },"
            + "{ 'comments.reactions.user_id': ?0 }"
            + "] }")
    List<Post> findPostsWithUserActivity(String userId);
}