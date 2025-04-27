package com.example.vibely_backend.controller;

import com.example.vibely_backend.dto.response.ApiResponse;
import com.example.vibely_backend.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getAllDashboardData() {
        try {
            Map<String, Object> dashboardData = new HashMap<>();

            // Lấy tổng số lượng
            dashboardData.put("totalUsers", adminDashboardService.getTotalUsers());
            dashboardData.put("totalPosts", adminDashboardService.getTotalPosts());
            dashboardData.put("totalDocuments", adminDashboardService.getTotalDocuments());
            dashboardData.put("totalInquiries", adminDashboardService.getTotalInquiries());

            // Lấy dữ liệu thống kê
            Map<String, Object> stats = adminDashboardService.getDashboardStats("month");

            // Đảm bảo mỗi mảng stats không null và có dữ liệu
            if (!stats.containsKey("postsStats") || stats.get("postsStats") == null) {
                stats.put("postsStats", new ArrayList<>());
            }
            if (!stats.containsKey("usersStats") || stats.get("usersStats") == null) {
                stats.put("usersStats", new ArrayList<>());
            }
            if (!stats.containsKey("inquiriesStats") || stats.get("inquiriesStats") == null) {
                stats.put("inquiriesStats", new ArrayList<>());
            }
            if (!stats.containsKey("documentsStats") || stats.get("documentsStats") == null) {
                stats.put("documentsStats", new ArrayList<>());
            }

            // Thêm dữ liệu thống kê vào response
            dashboardData.put("usersData", stats.get("usersStats"));
            dashboardData.put("postsData", stats.get("postsStats"));
            dashboardData.put("documentsData", stats.get("documentsStats"));
            dashboardData.put("inquiriesData", stats.get("inquiriesStats"));

            log.info("Dashboard data: {}", dashboardData);
            return ResponseEntity.ok(new ApiResponse("success", "Lấy dữ liệu dashboard thành công", dashboardData));
        } catch (Exception e) {
            log.error("Lỗi khi lấy dữ liệu dashboard: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse("error", e.getMessage(), null));
        }
    }

    @GetMapping("/dashboard/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getDashboardStats(@RequestParam(defaultValue = "month") String timeUnit) {
        try {
            Map<String, Object> stats = adminDashboardService.getDashboardStats(timeUnit);

            // Đảm bảo mỗi mảng stats không null và có dữ liệu
            if (!stats.containsKey("postsStats") || stats.get("postsStats") == null) {
                stats.put("postsStats", new ArrayList<>());
            }
            if (!stats.containsKey("usersStats") || stats.get("usersStats") == null) {
                stats.put("usersStats", new ArrayList<>());
            }
            if (!stats.containsKey("inquiriesStats") || stats.get("inquiriesStats") == null) {
                stats.put("inquiriesStats", new ArrayList<>());
            }
            if (!stats.containsKey("documentsStats") || stats.get("documentsStats") == null) {
                stats.put("documentsStats", new ArrayList<>());
            }

            log.info("Dashboard stats: {}", stats);
            return ResponseEntity.ok(new ApiResponse("success", "Lấy thống kê thành công", stats));
        } catch (Exception e) {
            log.error("Lỗi khi lấy thống kê: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse("error", e.getMessage(), null));
        }
    }

    @GetMapping("/dashboard/total-users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getTotalUsers() {
        try {
            long totalUsers = adminDashboardService.getTotalUsers();
            return ResponseEntity.ok(new ApiResponse("success", "Lấy tổng số người dùng thành công", totalUsers));
        } catch (Exception e) {
            log.error("Lỗi khi lấy tổng số người dùng: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse("error", e.getMessage(), null));
        }
    }

    @GetMapping("/dashboard/total-posts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getTotalPosts() {
        try {
            long totalPosts = adminDashboardService.getTotalPosts();
            return ResponseEntity.ok(new ApiResponse("success", "Lấy tổng số bài viết thành công", totalPosts));
        } catch (Exception e) {
            log.error("Lỗi khi lấy tổng số bài viết: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse("error", e.getMessage(), null));
        }
    }

    @GetMapping("/dashboard/total-documents")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getTotalDocuments() {
        try {
            long totalDocuments = adminDashboardService.getTotalDocuments();
            return ResponseEntity.ok(new ApiResponse("success", "Lấy tổng số tài liệu thành công", totalDocuments));
        } catch (Exception e) {
            log.error("Lỗi khi lấy tổng số tài liệu: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse("error", e.getMessage(), null));
        }
    }

    @GetMapping("/dashboard/total-inquiries")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getTotalInquiries() {
        try {
            long totalInquiries = adminDashboardService.getTotalInquiries();
            return ResponseEntity.ok(new ApiResponse("success", "Lấy tổng số câu hỏi thành công", totalInquiries));
        } catch (Exception e) {
            log.error("Lỗi khi lấy tổng số câu hỏi: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse("error", e.getMessage(), null));
        }
    }
}