package com.example.vibely_backend.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.vibely_backend.entity.Level;

@Repository
public interface LevelRepository extends MongoRepository<Level, String> {
    Optional<Level> findByName(String name);
    Optional<Level> findById(String id);
}
