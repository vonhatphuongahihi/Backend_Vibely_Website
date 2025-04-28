package com.example.vibely_backend.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.vibely_backend.dto.response.ApiResponse;
import com.example.vibely_backend.service.InquiryService;
import com.example.vibely_backend.service.JWTService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/inquiry")
@RequiredArgsConstructor
public class InquiryController {

    private final JWTService jwtService;

    private final InquiryService inquiryService;

    @PostMapping
    public ResponseEntity<ApiResponse> createInquiry(@RequestHeader("Authorization") String authHeader, @RequestBody Map<String, String> body) {
        String token = authHeader.replace("Bearer ", "");
        String userId = jwtService.extractUserIdFromToken(token);
        return ResponseEntity.ok(inquiryService.createInquiry(userId, body.get("message")));
    }

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
