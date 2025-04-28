package com.example.vibely_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.vibely_backend.entity.DocumentUser;

@Repository
public interface DocumentRepository extends MongoRepository<DocumentUser, String> {
    List<DocumentUser> findByLevelId(String levelId);
    List<DocumentUser> findBySubjectId(String subjectId);
    List<DocumentUser> findByTitleContainingIgnoreCase(String title);
    Optional<DocumentUser> findById(String id);
    boolean existsById(String id);    
}
