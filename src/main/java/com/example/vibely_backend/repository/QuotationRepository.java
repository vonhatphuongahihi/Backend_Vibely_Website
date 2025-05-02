package com.example.vibely_backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.vibely_backend.entity.Quotation;

import java.util.List;

@Repository
public interface QuotationRepository extends MongoRepository<Quotation, String> {
    List<Quotation> findByAuthor(String author);
}