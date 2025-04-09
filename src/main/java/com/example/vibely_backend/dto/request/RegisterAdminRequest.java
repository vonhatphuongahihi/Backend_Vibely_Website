package com.example.vibely_backend.dto.request;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterAdminRequest {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String phone;
    private String password;
    private String nationality;
    private String city;
    private String profilePicture;
    private String role = "admin";
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
