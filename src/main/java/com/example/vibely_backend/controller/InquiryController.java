package com.example.vibely_backend.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
