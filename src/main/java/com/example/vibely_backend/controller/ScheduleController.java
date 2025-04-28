package com.example.vibely_backend.controller;

import com.example.vibely_backend.dto.request.ScheduleRequest;
import com.example.vibely_backend.dto.response.ApiResponse;
import com.example.vibely_backend.entity.Schedule;
import com.example.vibely_backend.entity.User;
import com.example.vibely_backend.service.ScheduleService;
import com.example.vibely_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse> createSchedule(@RequestBody ScheduleRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            String userId = user.getId();

            Schedule schedule = scheduleService.createSchedule(userId, request);
            return ResponseEntity.ok(new ApiResponse("success", "Tạo lịch thành công", schedule));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", e.getMessage(), null));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getSchedules() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            log.debug("Tìm user với email: {}", email);

            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            String userId = user.getId();

            List<Schedule> schedules = scheduleService.getSchedulesByUserId(userId);
            return ResponseEntity.ok(new ApiResponse("success", "Lấy danh sách lịch thành công", schedules));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateSchedule(@PathVariable String id, @RequestBody ScheduleRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            log.debug("Tìm user với email: {}", email);

            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            String userId = user.getId();

            Schedule schedule = scheduleService.updateSchedule(id, userId, request);
            return ResponseEntity.ok(new ApiResponse("success", "Cập nhật lịch thành công", schedule));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteSchedule(@PathVariable String id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            log.debug("Tìm user với email: {}", email);

            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            String userId = user.getId();

            scheduleService.deleteSchedule(id, userId);
            return ResponseEntity.ok(new ApiResponse("success", "Xóa lịch thành công", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", e.getMessage(), null));
        }
    }

    @GetMapping("/{scheduleId}")
    public ResponseEntity<?> getScheduleById(
            @PathVariable String scheduleId,
            Authentication authentication) {
        try {
            String userId = authentication.getName();
            Schedule schedule = scheduleService.getScheduleById(userId, scheduleId);
            return ResponseEntity.ok(schedule);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Error getting schedule: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Error");
        }
    }
}