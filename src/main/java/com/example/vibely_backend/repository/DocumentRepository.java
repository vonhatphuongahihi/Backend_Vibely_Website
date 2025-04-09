package com.example.vibely_backend.repository;

import com.example.vibely_backend.entity.DocumentUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends MongoRepository<DocumentUser, String> {
}