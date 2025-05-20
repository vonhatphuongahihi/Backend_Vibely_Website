package com.example.vibely_backend.controller;

import com.example.vibely_backend.entity.Quotation;
import com.example.vibely_backend.dto.request.QuotationRequest;
import com.example.vibely_backend.service.QuotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/quotations")
@CrossOrigin(origins = { "http://localhost:3001", "http://localhost:3000", "http://127.0.0.1:3001",
        "http://127.0.0.1:3000", "https://vibely-study-social-website.vercel.app" }, allowCredentials = "true")
public class QuotationController {
    @Autowired
    private QuotationService quotationService;

    // Lấy tất cả danh ngôn
    @GetMapping
    public List<Quotation> getAllQuotations() {
        return quotationService.getAllQuotations();
    }

    // Lấy danh ngôn theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Quotation> getQuotationById(@PathVariable String id) {
        Optional<Quotation> quotation = quotationService.getQuotationById(id);
        return quotation.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Thêm danh ngôn mới
    @PostMapping
    public ResponseEntity<Quotation> createQuotation(@RequestBody QuotationRequest quotationRequest) {
        Quotation createdQuotation = quotationService.createQuotation(quotationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdQuotation);
    }

    // Cập nhật danh ngôn theo ID
    @PutMapping("/{id}")
    public ResponseEntity<Quotation> updateQuotation(@PathVariable String id,
            @RequestBody QuotationRequest quotationRequest) {
        Quotation updatedQuotation = quotationService.updateQuotation(id, quotationRequest);
        return ResponseEntity.ok(updatedQuotation);
    }

    // Lấy danh ngôn theo tác giả
    @GetMapping("/author/{author}")
    public List<Quotation> getQuotationsByAuthor(@PathVariable String author) {
        return quotationService.getQuotationsByAuthor(author);
    }

    // Lấy danh ngôn ngẫu nhiên
    @GetMapping("/random")
    public ResponseEntity<?> getRandomQuotation() {
        Quotation randomQuote = quotationService.getRandomQuotation();
        if (randomQuote == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không có danh ngôn nào!");
        }
        return ResponseEntity.ok(randomQuote);
    }
}
