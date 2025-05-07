package com.example.vibely_backend.repository;

import com.example.vibely_backend.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);
    List<User> findByUsernameIgnoreCaseContainingOrEmailIgnoreCaseContaining(String username, String email);
    
}