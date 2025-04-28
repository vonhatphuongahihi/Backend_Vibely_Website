package com.example.vibely_backend.controller;

import com.example.vibely_backend.dto.request.BioRequest;
import com.example.vibely_backend.dto.request.UserProfileUpdateRequest;
import com.example.vibely_backend.dto.response.ApiResponse;
import com.example.vibely_backend.entity.Bio;
import com.example.vibely_backend.entity.User;
import com.example.vibely_backend.repository.BioRepository;
import com.example.vibely_backend.repository.UserRepository;
import com.example.vibely_backend.service.CloudinaryService;
import com.example.vibely_backend.service.JWTService;
import com.example.vibely_backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = { "http://localhost:3001", "http://localhost:3000", "http://127.0.0.1:3001",
        "http://127.0.0.1:3000" })
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BioRepository bioRepository;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @GetMapping("/check-auth")
    public ResponseEntity<?> checkAuth() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()
                    && !"anonymousUser".equals(authentication.getPrincipal())) {
                String email = authentication.getName();
                return userService.findByEmail(email)
                        .map(user -> ResponseEntity.ok(new ApiResponse("success", "User is authenticated", user)))
                        .orElse(ResponseEntity.ok(new ApiResponse("error", "User not found", null)));
            }
            return ResponseEntity.ok(new ApiResponse("error", "User is not authenticated", null));
        } catch (Exception e) {
            logger.error("Error checking authentication status", e);
            return ResponseEntity.ok(new ApiResponse("error", "Error checking authentication status", null));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestHeader("Authorization") String authHeader,
            @ModelAttribute UserProfileUpdateRequest request,
            @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture,
            @RequestPart(value = "coverPicture", required = false) MultipartFile coverPicture) {
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
            if (request.getUsername() != null)
                user.setUsername(request.getUsername());
            if (request.getEmail() != null)
                user.setEmail(request.getEmail());
            if (request.getGender() != null)
                user.setGender(request.getGender());
            if (request.getDateOfBirth() != null) {
                user.setDateOfBirth(request.getDateOfBirth());
            }

            userRepository.save(user);

            return ResponseEntity.ok(new ApiResponse("success", "Cập nhật hồ sơ thành công", user));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", "Lỗi khi cập nhật hồ sơ", e.getMessage()));
        }
    }

    @PutMapping("/bio")
    public ResponseEntity<?> updateUserBio(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody BioRequest bioRequest) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String userId = jwtService.extractUserIdFromToken(token);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

            Bio bio = bioRepository.findByUser(user).orElseGet(() -> {
                Bio newBio = new Bio();
                newBio.setUser(user);
                newBio.setCreatedAt(new Date());
                return newBio;
            });

            bio.setBioText(bioRequest.getBioText());
            bio.setLiveIn(bioRequest.getLiveIn());
            bio.setRelationship(bioRequest.getRelationship());
            bio.setWorkplace(bioRequest.getWorkplace());
            bio.setEducation(bioRequest.getEducation());
            bio.setPhone(bioRequest.getPhone());
            bio.setHometown(bioRequest.getHometown());
            bio.setUpdatedAt(new Date());

            Bio savedBio = bioRepository.save(bio);

            user.setBio(savedBio);
            userRepository.save(user);

            return ResponseEntity.ok(new ApiResponse("success", "Cập nhật tiểu sử thành công", savedBio));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("error", "Lỗi khi cập nhật tiểu sử", e.getMessage()));
        }
    }
}
