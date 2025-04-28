package com.example.vibely_backend.dto.response;

import lombok.Data;
@Data
public class InquiryResponse {
    private String id;
    private String message;
    private String status;
    private String response;
    private String createdAt;
    private String updatedAt;

    // Thông tin user
    private String userId;
    private String username;
    private String email;
}