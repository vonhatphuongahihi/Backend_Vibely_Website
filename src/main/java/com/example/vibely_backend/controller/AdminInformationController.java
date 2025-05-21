package com.example.vibely_backend.controller;

import com.example.vibely_backend.entity.Admin;
import com.example.vibely_backend.entity.AdminPrincipal;
import com.example.vibely_backend.repository.AdminRepository;
import com.example.vibely_backend.dto.response.ApiResponse;
import com.example.vibely_backend.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;

@RestController
@RequestMapping("/admin/account")
public class AdminInformationController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    // Lấy thông tin admin dựa trên ID
    @GetMapping("/{adminId}")
    public ResponseEntity<?> getAdminInfo(@PathVariable String adminId) {
        try {
            Admin admin = adminRepository.findById(adminId).orElse(null);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse("error", "Admin không tồn tại", null));
            }
            admin.setPassword(null); // Ẩn password
            return ResponseEntity.ok(new ApiResponse("success", "Lấy thông tin admin thành công", admin));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("error", "Lỗi server", e.getMessage()));
        }
    }

    // Cập nhật thông tin admin 
    @PutMapping("/{adminId}")
    public ResponseEntity<?> updateAdminInfo(@PathVariable String adminId, @RequestBody Admin updatedData) {
        try {
            Admin admin = adminRepository.findById(adminId).orElse(null);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse("error", "Admin không tồn tại", null));
            }

            if (updatedData.getFirstName() != null)
                admin.setFirstName(updatedData.getFirstName());
            if (updatedData.getLastName() != null)
                admin.setLastName(updatedData.getLastName());
            if (updatedData.getUsername() != null)
                admin.setUsername(updatedData.getUsername());
            if (updatedData.getEmail() != null)
                admin.setEmail(updatedData.getEmail());
            if (updatedData.getPhone() != null)
                admin.setPhone(updatedData.getPhone());
            if (updatedData.getCity() != null)
                admin.setCity(updatedData.getCity());
            if (updatedData.getNationality() != null)
                admin.setNationality(updatedData.getNationality());

            adminRepository.save(admin);
            admin.setPassword(null); // Ẩn password khi trả về client
            return ResponseEntity.ok(new ApiResponse("success", "Cập nhật admin thành công", admin));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("error", "Lỗi server", e.getMessage()));
        }
    }

    // Xử lý upload ảnh đại diện
    @PostMapping("/avatar/{adminId}")
    public ResponseEntity<?> uploadProfilePicture(
            @PathVariable String adminId,
            @RequestParam("profilePicture") MultipartFile file) {
        try {
            // Kiểm tra xem file có tồn tại và không rỗng
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse("error", "Vui lòng chọn ảnh.", null));
            }

            // Tải ảnh lên Cloudinary
            Map<String, Object> uploadResult = cloudinaryService.uploadFile(file, "admin-avatars");
            if (uploadResult == null || uploadResult.get("secure_url") == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse("error", "Lỗi khi tải ảnh lên.", null));
            }

            // Lấy URL của ảnh đại diện
            String profilePictureUrl = (String) uploadResult.get("secure_url");
            Admin admin = adminRepository.findById(adminId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy admin"));

            // Cập nhật ảnh đại diện cho admin
            admin.setProfilePicture(profilePictureUrl);
            adminRepository.save(admin);

            // Trả về phản hồi thành công
            return ResponseEntity.ok()
                    .body(new ApiResponse("success", "Ảnh đại diện đã được cập nhật.",
                            Map.of("profilePicture", profilePictureUrl)));
        } catch (Exception e) {
            // Xử lý lỗi server
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("error", "Lỗi server: " + e.getMessage(), null));
        }
    }
}