package com.example.vibely_backend.service;

import com.example.vibely_backend.entity.Quotation;
import com.example.vibely_backend.repository.QuotationRepository;
import com.example.vibely_backend.dto.request.QuotationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuotationService {

    @Autowired
    private QuotationRepository quotationRepository;

    // Lấy tất cả danh ngôn
    public List<Quotation> getAllQuotations() {
        return quotationRepository.findAll();
    }

    // Lấy danh ngôn theo ID
    public Optional<Quotation> getQuotationById(String id) {
        return quotationRepository.findById(id);
    }

    // Tạo danh ngôn mới
    public Quotation createQuotation(QuotationRequest quotationRequest) {
        Quotation newQuotation = new Quotation();
        newQuotation.setText(quotationRequest.getText());
        newQuotation.setAuthor(quotationRequest.getAuthor());
        return quotationRepository.save(newQuotation);
    }

    // Cập nhật danh ngôn
    public Quotation updateQuotation(String id, QuotationRequest quotationRequest) {
        return quotationRepository.findById(id).map(quotation -> {
            quotation.setText(quotationRequest.getText());
            quotation.setAuthor(quotationRequest.getAuthor());
            return quotationRepository.save(quotation);
        }).orElseGet(() -> {
            Quotation newQuotation = new Quotation();
            newQuotation.setId(id);
            newQuotation.setText(quotationRequest.getText());
            newQuotation.setAuthor(quotationRequest.getAuthor());
            return quotationRepository.save(newQuotation);
        });
    }

    // Lấy danh ngôn theo tác giả
    public List<Quotation> getQuotationsByAuthor(String author) {
        return quotationRepository.findByAuthor(author);
    }

    // Lấy danh ngôn ngẫu nhiên
    public Quotation getRandomQuotation() {
        long count = quotationRepository.count();
        if (count == 0)
            return null;

        int randomIndex = (int) (Math.random() * count);
        Pageable pageable = PageRequest.of(randomIndex, 1);
        List<Quotation> randomQuotations = quotationRepository.findAll(pageable).getContent();
        return randomQuotations.isEmpty() ? null : randomQuotations.get(0);
    }
}
