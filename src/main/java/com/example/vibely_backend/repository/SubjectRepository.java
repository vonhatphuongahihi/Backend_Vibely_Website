package com.example.vibely_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.vibely_backend.entity.Level;
import com.example.vibely_backend.entity.Subject;

@Repository
public interface SubjectRepository extends MongoRepository<Subject, String> {
    Optional<Subject> findById(String id);
    List<Subject> findByLevel(Level level);
    Optional<Subject> findByNameAndLevel(String name, Level level);
}
