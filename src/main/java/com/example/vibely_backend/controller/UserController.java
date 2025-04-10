package com.example.vibely_backend.controller;

import com.example.vibely_backend.dto.request.UserProfileUpdateRequest;
import com.example.vibely_backend.dto.response.ApiResponse;
import com.example.vibely_backend.entity.User;
import com.example.vibely_backend.repository.UserRepository;
import com.example.vibely_backend.service.CloudinaryService;
import com.example.vibely_backend.service.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTService jwtService;
    
    @Autowired
    private CloudinaryService cloudinaryService;

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
        @RequestHeader("Authorization") String authHeader,
        @ModelAttribute UserProfileUpdateRequest request,
        @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture,
        @RequestPart(value = "coverPicture", required = false) MultipartFile coverPicture
) {
    try {
        String token = authHeader.replace("Bearer ", "");
        String userId = jwtService.extractUserIdFromToken(token);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        // Upload avatar nếu có
        if (profilePicture != null && !profilePicture.isEmpty()) {
            Map<String, Object> avatarUpload = cloudinaryService.uploadFile(profilePicture, "user-avatars");
            user.setProfilePicture((String) avatarUpload.get("secure_url"));
        }

        // Upload cover nếu có
        if (coverPicture != null && !coverPicture.isEmpty()) {
            Map<String, Object> coverUpload = cloudinaryService.uploadFile(coverPicture, "user-covers");
            user.setCoverPicture((String) coverUpload.get("secure_url"));
        }

        // Cập nhật thông tin từ DTO
        if (request.getUsername() != null) user.setUsername(request.getUsername());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getGender() != null) user.setGender(request.getGender());
        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }

        userRepository.save(user);

        return ResponseEntity.ok(new ApiResponse(200, "Cập nhật hồ sơ thành công", user));

    } catch (Exception e) {
        return ResponseEntity.badRequest().body(new ApiResponse(400, "Lỗi khi cập nhật hồ sơ", e.getMessage()));
    }
}

}