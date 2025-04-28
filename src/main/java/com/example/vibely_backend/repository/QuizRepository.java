package com.example.vibely_backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.vibely_backend.entity.Quiz;

@Repository
public interface QuizRepository extends MongoRepository<Quiz, String> {
}
