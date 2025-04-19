package com.example.vibely_backend.controller;

import com.example.vibely_backend.entity.Admin;
import com.example.vibely_backend.repository.AdminRepository;
import com.example.vibely_backend.dto.response.ApiResponse;
import com.example.vibely_backend.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/account")
public class AdminInformationController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    // Lấy thông tin admin dựa trên ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getAdminInfo(@PathVariable String id) {
        try {
            Admin admin = adminRepository.findById(id).orElse(null);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(404, "Admin không tồn tại", null));
            }
            admin.setPassword(null); // Ẩn password
            return ResponseEntity.ok(new ApiResponse(200, "Lấy thông tin admin thành công", admin));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(500, "Lỗi server", e.getMessage()));
        }
    }

    // Cập nhật thông tin admin
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAdminInfo(@PathVariable String id, @RequestBody Admin updatedData) {
        try {
            Admin admin = adminRepository.findById(id).orElse(null);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(404, "Admin không tồn tại", null));
            }

            if (updatedData.getFirstName() != null) admin.setFirstName(updatedData.getFirstName());
            if (updatedData.getLastName() != null) admin.setLastName(updatedData.getLastName());
            if (updatedData.getUsername() != null) admin.setUsername(updatedData.getUsername());
            if (updatedData.getEmail() != null) admin.setEmail(updatedData.getEmail());
            if (updatedData.getPhone() != null) admin.setPhone(updatedData.getPhone());
            if (updatedData.getCity() != null) admin.setCity(updatedData.getCity());
            if (updatedData.getNationality() != null) admin.setNationality(updatedData.getNationality());

            adminRepository.save(admin);
            admin.setPassword(null); // Ẩn password khi trả về client
            return ResponseEntity.ok(new ApiResponse(200, "Cập nhật admin thành công", admin));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(500, "Lỗi server", e.getMessage()));
        }
    }

    // Xử lý upload ảnh đại diện
    @PostMapping("/avatar/{adminId}")
    public ResponseEntity<?> uploadProfilePicture(
    @PathVariable String adminId,
    @RequestParam("profilePicture") MultipartFile file
    ) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse(400, "Vui lòng chọn ảnh.", null));
            }

            Map<String, Object> uploadResult = cloudinaryService.uploadFile(file, "admin-avatars");
            if (uploadResult == null || uploadResult.get("secure_url") == null) {
                return ResponseEntity.badRequest().body(new ApiResponse(400, "Lỗi khi tải ảnh lên.", null));
            }

            String profilePictureUrl = (String) uploadResult.get("secure_url");
            Admin admin = adminRepository.findById(adminId).orElseThrow(() -> new RuntimeException("Không tìm thấy admin"));

            admin.setProfilePicture(profilePictureUrl);
            adminRepository.save(admin);

            return ResponseEntity.ok(new ApiResponse(200, "Ảnh đại diện đã được cập nhật.", Map.of("profilePicture", profilePictureUrl)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(500, "Lỗi server", e.getMessage()));
        }
    }

    private String getAdminId() {
        // Lấy ID của admin từ token hoặc bất kỳ nguồn nào khác
        return "adminId";
    }
}