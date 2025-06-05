package com.example.vibely_backend.repository;

import com.example.vibely_backend.entity.Bio;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BioRepository extends MongoRepository<Bio, String> {
    Optional<Bio> findByUserId(String userId);

    List<Bio> findAllByUserId(String userId);
}
