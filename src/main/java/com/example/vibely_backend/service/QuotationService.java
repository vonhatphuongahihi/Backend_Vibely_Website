package com.example.vibely_backend.service;

import com.example.vibely_backend.entity.Quotation;
import com.example.vibely_backend.repository.QuotationRepository;
import com.example.vibely_backend.dto.request.QuotationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class QuotationService {

    @Autowired
    private QuotationRepository quotationRepository;

    // Lấy tất cả danh ngôn
    public List<Quotation> getAllQuotations() {
        List<Quotation> quotations = quotationRepository.findAll();
        return quotations;
    }

    // Lấy danh ngôn theo ID
    public Optional<Quotation> getQuotationById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID danh ngôn không hợp lệ");
        }
        return quotationRepository.findById(id);
    }

    // Tạo danh ngôn mới
    public Quotation createQuotation(QuotationRequest quotationRequest) {
        if (quotationRequest == null) {
            throw new IllegalArgumentException("Thông tin danh ngôn không được để trống");
        }

        if (quotationRequest.getText() == null || quotationRequest.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Nội dung danh ngôn không được để trống");
        }

        Quotation newQuotation = new Quotation();
        newQuotation.setText(quotationRequest.getText().trim());
        newQuotation.setAuthor(quotationRequest.getAuthor() != null ? quotationRequest.getAuthor().trim() : "Unknown");

        Quotation saved = quotationRepository.save(newQuotation);
        return saved;
    }

    // Cập nhật danh ngôn
    public Quotation updateQuotation(String id, QuotationRequest quotationRequest) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID danh ngôn không hợp lệ");
        }

        if (quotationRequest == null) {
            throw new IllegalArgumentException("Thông tin danh ngôn không được để trống");
        }

        if (quotationRequest.getText() == null || quotationRequest.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Nội dung danh ngôn không được để trống");
        }

        return quotationRepository.findById(id).map(quotation -> {
            quotation.setText(quotationRequest.getText().trim());
            quotation.setAuthor(quotationRequest.getAuthor() != null ? quotationRequest.getAuthor().trim() : "Unknown");
            Quotation updated = quotationRepository.save(quotation);
            log.info("Đã cập nhật danh ngôn với ID: {}", updated.getId());
            return updated;
        }).orElseGet(() -> {
            Quotation newQuotation = new Quotation();
            newQuotation.setId(id);
            newQuotation.setText(quotationRequest.getText().trim());
            newQuotation
                    .setAuthor(quotationRequest.getAuthor() != null ? quotationRequest.getAuthor().trim() : "Unknown");
            Quotation saved = quotationRepository.save(newQuotation);
            log.info("Đã tạo danh ngôn mới với ID: {}", saved.getId());
            return saved;
        });
    }

    // Lấy danh ngôn theo tác giả
    public List<Quotation> getQuotationsByAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên tác giả không hợp lệ");
        }
        List<Quotation> quotations = quotationRepository.findByAuthor(author.trim());
        return quotations;
    }

    // Lấy danh ngôn ngẫu nhiên
    public Quotation getRandomQuotation() {
        long count = quotationRepository.count();
        if (count == 0) {
            log.warn("Không có danh ngôn nào trong database");
            return null;
        }

        int randomIndex = (int) (Math.random() * count);
        Pageable pageable = PageRequest.of(randomIndex, 1);
        List<Quotation> randomQuotations = quotationRepository.findAll(pageable).getContent();
        Quotation quotation = randomQuotations.isEmpty() ? null : randomQuotations.get(0);
        log.info("Đã lấy danh ngôn ngẫu nhiên với ID: {}", quotation != null ? quotation.getId() : "null");
        return quotation;
    }
}
