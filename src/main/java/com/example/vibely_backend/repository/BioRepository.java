package com.example.vibely_backend.repository;

import com.example.vibely_backend.entity.Bio;
import com.example.vibely_backend.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface BioRepository extends MongoRepository<Bio, String> {
    Optional<Bio> findByUser(User user);
}
