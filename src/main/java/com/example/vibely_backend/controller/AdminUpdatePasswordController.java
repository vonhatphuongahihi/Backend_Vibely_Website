package com.example.vibely_backend.controller;

import com.example.vibely_backend.dto.request.UpdatePasswordRequest;
import com.example.vibely_backend.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

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
            String username = authentication.getName();
            adminService.updatePassword(username, request.getOldPassword(), request.getNewPassword());
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Cập nhật mật khẩu thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("Không thể cập nhật mật khẩu: " + e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}