package com.example.vibely_backend.repository;

import com.example.vibely_backend.entity.GoogleCalendarToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface GoogleCalendarTokenRepository extends MongoRepository<GoogleCalendarToken, String> {
    Optional<GoogleCalendarToken> findByUserId(String userId);

    void deleteByUserId(String userId);
}