package com.example.vibely_backend.repository;

import com.example.vibely_backend.entity.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    List<Post> findByUserIdOrderByCreatedAtDesc(String userId);

    void deleteByUserId(String userId);
}