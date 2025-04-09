package com.example.vibely_backend.controller;

import com.example.vibely_backend.dto.request.UpdatePasswordRequest;
import com.example.vibely_backend.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminUpdatePasswordController {

    private final AdminService adminService;

    @PutMapping("/change-password")
    public ResponseEntity<?> updatePassword(
            @Valid @RequestBody UpdatePasswordRequest request,
            Authentication authentication) {
        try {
            // Lấy username từ token
            String username = authentication.getName();
            System.out.println("Updating password for admin: " + username);

            adminService.updatePassword(username, request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.ok().body("Password updated successfully");
        } catch (Exception e) {
            System.out.println("Error updating password: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}