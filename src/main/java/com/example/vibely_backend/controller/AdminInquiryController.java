package com.example.vibely_backend.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.vibely_backend.dto.response.ApiResponse;
import com.example.vibely_backend.service.InquiryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/inquiry")
@RequiredArgsConstructor
public class AdminInquiryController {

    private final InquiryService inquiryService;

    @GetMapping
    public ResponseEntity<ApiResponse> getInquiries(
        @RequestParam(required = false) String query,
        @RequestParam(required = false) String status
    ) {
        return ResponseEntity.ok(inquiryService.getInquiries(query, status));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateInquiry(
        @PathVariable String id,
        @RequestBody Map<String, String> body
    ) {
        return ResponseEntity.ok(inquiryService.updateInquiry(id, body.get("status"), body.get("response")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteInquiry(@PathVariable String id) {
        return ResponseEntity.ok(inquiryService.deleteInquiry(id));
    }
}
