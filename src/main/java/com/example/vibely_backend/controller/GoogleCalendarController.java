package com.example.vibely_backend.controller;

import com.example.vibely_backend.service.GoogleCalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/google-calendar")
@RequiredArgsConstructor
public class GoogleCalendarController {
    private final GoogleCalendarService googleCalendarService;

    // Endpoint lấy link xác thực
    @GetMapping("/auth-url")
    public ResponseEntity<?> getGoogleCalendarAuthUrl() {
        try {
            String url = googleCalendarService.buildAuthUrl();
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi tạo link xác thực Google Calendar: " + e.getMessage());
        }
    }

    // Endpoint nhận callback từ Google
    @GetMapping("/oauth2callback")
    public ResponseEntity<?> handleGoogleCalendarCallback(@RequestParam("code") String code) {
        try {
            googleCalendarService.handleAuthCallback(code);
            // Có thể redirect về frontend với thông báo thành công
            return ResponseEntity.ok("Xác thực Google Calendar thành công! Bạn có thể đóng tab này.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi xác thực Google Calendar: " + e.getMessage());
        }
    }
}
