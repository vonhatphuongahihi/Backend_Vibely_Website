package com.example.vibely_backend.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.vibely_backend.entity.Inquiry;

@Repository
public interface InquiryRepository extends MongoRepository<Inquiry, String> {
    List<Inquiry> findByUserId(String userId);

    List<Inquiry> findByStatus(String status);

    void deleteByUserId(String userId);
}