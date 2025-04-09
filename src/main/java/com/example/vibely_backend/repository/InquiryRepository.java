package com.example.vibely_backend.repository;

import com.example.vibely_backend.entity.Inquiry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InquiryRepository extends MongoRepository<Inquiry, String> {
    List<Inquiry> findByUserId(String userId);

    List<Inquiry> findByStatus(String status);
}