package com.example.vibely_backend.controller;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.vibely_backend.dto.request.BioRequest;
import com.example.vibely_backend.dto.request.UserProfileUpdateRequest;
import com.example.vibely_backend.dto.response.ApiResponse;
import com.example.vibely_backend.dto.response.UserInfoResponse;
import com.example.vibely_backend.entity.Bio;
import com.example.vibely_backend.entity.User;
import com.example.vibely_backend.repository.BioRepository;
import com.example.vibely_backend.repository.UserRepository;
import com.example.vibely_backend.service.CloudinaryService;
import com.example.vibely_backend.service.JWTService;
import com.example.vibely_backend.service.UserService;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = {
        "http://localhost:3001", "http://localhost:3000",
        "http://127.0.0.1:3001", "http://127.0.0.1:3000",
        "https://vibely-study-social-website.vercel.app",
        "https://vibely-study-social-admin-website.vercel.app"
})
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private MongoTemplate mongoTemplate;
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

    private String getUserIdFromAuthHeader(String authHeader) {
        return jwtService.extractUserIdFromToken(authHeader.replace("Bearer ", ""));
    }

    @GetMapping("/check-auth")
    public ResponseEntity<ApiResponse> checkAuth(Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401)
                        .body(new ApiResponse("error", "Unauthorized", null));
            }

            String email = authentication.getName();
            Optional<User> userOpt = userService.findByEmail(email);

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                UserInfoResponse userInfo = userService.convertToUserInfoResponse(user);
                return ResponseEntity.ok(new ApiResponse("success", "User authenticated", userInfo));
            }

            return ResponseEntity.status(401)
                    .body(new ApiResponse("error", "User not found", null));
        } catch (Exception e) {
            logger.error("Error checking authentication status", e);
            return ResponseEntity.status(500)
                    .body(new ApiResponse("error", "Internal server error", e.getMessage()));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestHeader("Authorization") String authHeader,
            @ModelAttribute UserProfileUpdateRequest request,
            @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture,
            @RequestPart(value = "coverPicture", required = false) MultipartFile coverPicture) {
        try {
            String userId = getUserIdFromAuthHeader(authHeader);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

            if (profilePicture != null && !profilePicture.isEmpty()) {
                if (!profilePicture.getContentType().startsWith("image/")) {
                    return ResponseEntity.badRequest()
                            .body(new ApiResponse("error", "Ảnh đại diện không hợp lệ", null));
                }
                Map<String, Object> avatarUpload = cloudinaryService.uploadFile(profilePicture, "user-avatars");
                user.setProfilePicture((String) avatarUpload.get("secure_url"));
            }

            if (coverPicture != null && !coverPicture.isEmpty()) {
                if (!coverPicture.getContentType().startsWith("image/")) {
                    return ResponseEntity.badRequest().body(new ApiResponse("error", "Ảnh bìa không hợp lệ", null));
                }
                Map<String, Object> coverUpload = cloudinaryService.uploadFile(coverPicture, "user-covers");
                user.setCoverPicture((String) coverUpload.get("secure_url"));
            }

            if (request.getUsername() != null)
                user.setUsername(request.getUsername());
            if (request.getEmail() != null)
                user.setEmail(request.getEmail());
            if (request.getGender() != null)
                user.setGender(request.getGender());
            if (request.getDateOfBirth() != null)
                user.setDateOfBirth(request.getDateOfBirth());

            userRepository.save(user);
            UserInfoResponse userReponse = userService.convertToUserInfoResponse(user);

            return ResponseEntity.ok(new ApiResponse("success", "Cập nhật hồ sơ thành công", userReponse));

        } catch (Exception e) {
            logger.error("Lỗi khi cập nhật hồ sơ", e);
            return ResponseEntity.badRequest().body(new ApiResponse("error", "Lỗi khi cập nhật hồ sơ", e.getMessage()));
        }
    }

    @PutMapping("/bio")
    public ResponseEntity<?> updateUserBio(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody BioRequest bioRequest) {
        try {
            String userId = getUserIdFromAuthHeader(authHeader);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

            Bio bio = bioRepository.findByUserId(userId).orElseGet(() -> {
                Bio newBio = new Bio();
                newBio.setUserId(userId);
                newBio.setCreatedAt(new Date());
                return newBio;
            });

            if (bioRequest.getBioText() != null && !bioRequest.getBioText().isBlank())
                bio.setBioText(bioRequest.getBioText());
            if (bioRequest.getLiveIn() != null && !bioRequest.getLiveIn().isBlank())
                bio.setLiveIn(bioRequest.getLiveIn());
            if (bioRequest.getRelationship() != null && !bioRequest.getRelationship().isBlank())
                bio.setRelationship(bioRequest.getRelationship());
            if (bioRequest.getWorkplace() != null && !bioRequest.getWorkplace().isBlank())
                bio.setWorkplace(bioRequest.getWorkplace());
            if (bioRequest.getEducation() != null && !bioRequest.getEducation().isBlank())
                bio.setEducation(bioRequest.getEducation());
            if (bioRequest.getHometown() != null && !bioRequest.getHometown().isBlank())
                bio.setHometown(bioRequest.getHometown());

            bio.setUpdatedAt(new Date());
            Bio savedBio = bioRepository.save(bio);

            return ResponseEntity.ok(new ApiResponse("success", "Cập nhật tiểu sử thành công", savedBio));
        } catch (Exception e) {
            logger.error("Lỗi khi cập nhật tiểu sử", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("error", "Lỗi khi cập nhật tiểu sử", e.getMessage()));
        }
    }

    @PostMapping("/follow")
    public ResponseEntity<ApiResponse> followUser(@RequestBody Map<String, String> requestBody) {
        String userIdToFollow = requestBody.get("userIdToFollow");
        return ResponseEntity.ok(userService.followUser(userIdToFollow));
    }

    @PostMapping("/unfollow")
    public ResponseEntity<ApiResponse> unfollowUser(@RequestBody Map<String, String> requestBody) {
        String userIdToUnfollow = requestBody.get("userIdToUnfollow");
        return ResponseEntity.ok(userService.unfollowUser(userIdToUnfollow));
    }

    @PostMapping("/friend-request/remove")
    public ResponseEntity<ApiResponse> deleteFriendRequest(@RequestBody Map<String, String> requestBody) {
        String requestSenderId = requestBody.get("requestSenderId");
        return ResponseEntity.ok(userService.deleteFriendRequest(requestSenderId));
    }

    @GetMapping("/friend-request")
    public ResponseEntity<ApiResponse> getAllFriendRequests() {
        return ResponseEntity.ok(userService.getAllFriendRequests());
    }

    @GetMapping("/user-to-request")
    public ResponseEntity<ApiResponse> getAllUsersForFriendRequest() {
        return ResponseEntity.ok(userService.getAllUserForRequest());
    }

    @GetMapping("/mutual-friends/{userId}")
    public ResponseEntity<ApiResponse> getMutualFriends(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getMutualFriends(userId));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<ApiResponse> getUserProfile(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

    @PostMapping("/get-users")
    public ResponseEntity<ApiResponse> getUsersByIds(@RequestBody Map<String, List<String>> body) {
        List<String> userIds = body.get("userIds");
        return ResponseEntity.ok(userService.getUsersByIds(userIds));
    }

    @GetMapping("/saved")
    public ResponseEntity<ApiResponse> getSavedDocuments(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String subject) {
        return ResponseEntity.ok(userService.getSavedDocuments(query, level, subject));
    }

    @GetMapping("/saved/user-profile/{userId}")
    public ResponseEntity<ApiResponse> getSavedDocumentsByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getSavedDocumentsByUserId(userId));
    }

    @GetMapping("/saved/{id}")
    public ResponseEntity<ApiResponse> getSavedDocumentById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getSavedDocumentById(id));
    }

    @DeleteMapping("/saved/{id}")
    public ResponseEntity<ApiResponse> unsaveDocument(@PathVariable String id) {
        return ResponseEntity.ok(userService.unsaveDocument(id));
    }

    // Google Calendar endpoints
    @PostMapping("/google-calendar/connect")
    public ResponseEntity<ApiResponse> connectGoogleCalendar(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> tokens) {
        try {
            String userId = getUserIdFromAuthHeader(authHeader);
            String accessToken = tokens.get("accessToken");
            String refreshToken = tokens.get("refreshToken");
            LocalDateTime expiry = LocalDateTime.parse(tokens.get("expiry"));

            userService.saveGoogleCalendarTokens(userId, accessToken, refreshToken, expiry);
            return ResponseEntity.ok(new ApiResponse("success", "Google Calendar connected successfully", null));
        } catch (Exception e) {
            logger.error("Error connecting Google Calendar", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("error", "Failed to connect Google Calendar", e.getMessage()));
        }
    }

    @PostMapping("/google-calendar/disconnect")
    public ResponseEntity<ApiResponse> disconnectGoogleCalendar(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String userId = getUserIdFromAuthHeader(authHeader);
            userService.disconnectGoogleCalendar(userId);
            return ResponseEntity.ok(new ApiResponse("success", "Google Calendar disconnected successfully", null));
        } catch (Exception e) {
            logger.error("Error disconnecting Google Calendar", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("error", "Failed to disconnect Google Calendar", e.getMessage()));
        }
    }

    @GetMapping("/google-calendar/status")
    public ResponseEntity<ApiResponse> getGoogleCalendarStatus(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String userId = getUserIdFromAuthHeader(authHeader);
            boolean isConnected = userService.isGoogleCalendarConnected(userId);
            return ResponseEntity.ok(new ApiResponse("success", "Google Calendar status retrieved", isConnected));
        } catch (Exception e) {
            logger.error("Error getting Google Calendar status", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("error", "Failed to get Google Calendar status", e.getMessage()));
        }
    }

    @GetMapping("/info/{userId}")
    public ResponseEntity<?> getUserInfo(@PathVariable String userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return ResponseEntity.ok(new ApiResponse("success", "Lấy thông tin user thành công", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("error", "Lỗi khi lấy thông tin user", e.getMessage()));
        }
    }
}
