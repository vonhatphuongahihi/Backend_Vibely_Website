package com.example.vibely_backend.controller;

import com.example.vibely_backend.dto.response.ApiResponse;
import com.example.vibely_backend.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getAdminDashboard(@RequestParam(defaultValue = "month") String timeUnit) {
        try {
            Map<String, Object> dashboardData = adminDashboardService.getAdminDashboard(timeUnit);
            return ResponseEntity.ok(new ApiResponse(200, "Lấy thông tin tổng quan thành công", dashboardData));
        } catch (Exception e) {
            log.error("Lỗi khi lấy thông tin tổng quan: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse(400, e.getMessage(), null));
        }
    }

    @GetMapping("/dashboard/total-users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getTotalUsers() {
        try {
            long totalUsers = adminDashboardService.getTotalUsers();
            return ResponseEntity.ok(new ApiResponse(200, "Lấy tổng số người dùng thành công", totalUsers));
        } catch (Exception e) {
            log.error("Lỗi khi lấy tổng số người dùng: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse(400, e.getMessage(), null));
        }
    }

    @GetMapping("/dashboard/total-posts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getTotalPosts() {
        try {
            long totalPosts = adminDashboardService.getTotalPosts();
            return ResponseEntity.ok(new ApiResponse(200, "Lấy tổng số bài viết thành công", totalPosts));
        } catch (Exception e) {
            log.error("Lỗi khi lấy tổng số bài viết: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse(400, e.getMessage(), null));
        }
    }

    @GetMapping("/dashboard/total-documents")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getTotalDocuments() {
        try {
            long totalDocuments = adminDashboardService.getTotalDocuments();
            return ResponseEntity.ok(new ApiResponse(200, "Lấy tổng số tài liệu thành công", totalDocuments));
        } catch (Exception e) {
            log.error("Lỗi khi lấy tổng số tài liệu: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse(400, e.getMessage(), null));
        }
    }

    @GetMapping("/dashboard/total-inquiries")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getTotalInquiries() {
        try {
            long totalInquiries = adminDashboardService.getTotalInquiries();
            return ResponseEntity.ok(new ApiResponse(200, "Lấy tổng số câu hỏi thành công", totalInquiries));
        } catch (Exception e) {
            log.error("Lỗi khi lấy tổng số câu hỏi: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse(400, e.getMessage(), null));
        }
    }

    @GetMapping("/dashboard/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getDashboardStats(@RequestParam(defaultValue = "month") String timeUnit) {
        try {
            Map<String, Object> stats = adminDashboardService.getDashboardStats(timeUnit);
            return ResponseEntity.ok(new ApiResponse(200, "Lấy thống kê thành công", stats));
        } catch (Exception e) {
            log.error("Lỗi khi lấy thống kê: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse(400, e.getMessage(), null));
        }
    }
}