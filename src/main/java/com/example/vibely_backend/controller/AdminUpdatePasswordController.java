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

    @PutMapping("/auth/update-password")
    public ResponseEntity<?> updatePassword(
            @Valid @RequestBody UpdatePasswordRequest request,
            Authentication authentication) {
        try {
            // Lấy username từ token
            String username = authentication.getName();

            adminService.updatePassword(username, request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.ok().body("Cập nhật mật khẩu thành công");
        } catch (Exception e) {
            System.out.println("Không thể cập nhật mật khẩu: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}