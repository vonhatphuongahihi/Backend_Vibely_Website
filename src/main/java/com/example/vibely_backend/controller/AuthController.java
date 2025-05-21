package com.example.vibely_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.vibely_backend.entity.User;
import com.example.vibely_backend.service.UserService;
import com.example.vibely_backend.dto.request.RegisterRequest;
import com.example.vibely_backend.dto.request.LoginRequest;
import com.example.vibely_backend.dto.request.ChangePasswordRequest;
import com.example.vibely_backend.dto.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import com.example.vibely_backend.service.JWTService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import com.example.vibely_backend.service.oauth2.OAuth2UserDetails;
import com.example.vibely_backend.service.oauth2.OAuth2GoogleUser;
import com.example.vibely_backend.service.oauth2.OAuth2FacebookUser;
import com.example.vibely_backend.service.oauth2.OAuth2GithubUser;
import com.example.vibely_backend.entity.Provider;
import org.springframework.web.servlet.view.RedirectView;
import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@Slf4j
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = { "http://localhost:3001", "http://localhost:3000", "http://127.0.0.1:3001",
        "http://127.0.0.1:3000", "https://vibely-study-social-website.vercel.app" }, allowCredentials = "true")
public class AuthController {

    @Autowired
    private UserService service;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTService jwtService;

    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse> sendOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        // Kiểm tra email
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", "Email không được để trống", null));
        }

        try {
            // Gửi OTP đến email
            service.sendOtpToEmail(email); // gọi từ UserService
            return ResponseEntity.ok(new ApiResponse("success", "Mã xác thực đã được gửi đến email", null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("error", "Gửi mã xác thực thất bại", e.getMessage()));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");

        // Kiểm tra email
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", "Email không được để trống", null));
        }

        try {
            // Xác thực OTP
            if (service.verifyOtp(email, otp)) {
                return ResponseEntity.ok(new ApiResponse("success", "Xác thực OTP thành công", null));
            } else {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse("error", "OTP không hợp lệ hoặc đã hết hạn", null));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("error", "Xác thực OTP thất bại", e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody RegisterRequest registerRequest) {
        try {
            User user = new User();
            user.setUsername(registerRequest.getUsername());
            user.setEmail(registerRequest.getEmail());
            user.setPassword(registerRequest.getPassword());
            user.setGender(registerRequest.getGender());
            user.setDateOfBirth(registerRequest.getDateOfBirthAsLocalDate());

            // Set createdAt và updatedAt cho user mới
            LocalDateTime now = LocalDateTime.now();
            user.setCreatedAt(now);
            user.setUpdatedAt(now);

            User registeredUser = service.register(user);

            String token = service.generateToken(registeredUser);

            Map<String, Object> userData = new HashMap<>();
            userData.put("username", registeredUser.getUsername());
            userData.put("email", registeredUser.getEmail());
            userData.put("token", token);

            return ResponseEntity.status(201).body(new ApiResponse("success", "Đăng ký thành công", userData));
        } catch (RuntimeException e) {
            log.error("Đăng ký thất bại: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse("error", "Đăng ký thất bại", e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi không xác định trong quá trình đăng ký: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(new ApiResponse("error", "Lỗi hệ thống", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            String token = jwtService.generateToken(loginRequest.getEmail());

            User user = service.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy email đăng ký của người dùng"));

            Map<String, Object> data = new HashMap<>();
            data.put("userId", user.getId());
            data.put("email", user.getEmail());
            data.put("username", user.getUsername());
            data.put("token", token);

            return ResponseEntity.ok(new ApiResponse("success", "Đăng nhập thành công", data));
        } catch (Exception e) {
            log.error("Đăng nhập thất bại: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse("error", "Đăng nhập thất bại", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout() {
        return ResponseEntity.ok(new ApiResponse("success", "Đăng xuất thành công", null));
    }

    @GetMapping("/google")
    public RedirectView googleLogin() {
        return new RedirectView("/oauth2/authorize/google");
    }

    @GetMapping("/facebook")
    public RedirectView facebookLogin() {
        return new RedirectView("/oauth2/authorize/facebook");
    }

    @GetMapping("/github")
    public RedirectView githubLogin() {
        return new RedirectView("/oauth2/authorize/github");
    }

    @GetMapping("/google/callback")
    public ResponseEntity<?> googleCallback(OAuth2AuthenticationToken authentication) {
        try {
            if (authentication == null) {
                throw new RuntimeException("Không thể xác thực OAuth2");
            }
            OAuth2User oauth2User = authentication.getPrincipal();
            OAuth2UserDetails oauth2UserDetails = new OAuth2GoogleUser(oauth2User.getAttributes());

            // Set createdAt và updatedAt cho user OAuth2
            LocalDateTime now = LocalDateTime.now();
            oauth2UserDetails.setCreatedAt(now);
            oauth2UserDetails.setUpdatedAt(now);

            User user = service.processOAuth2User(oauth2UserDetails, Provider.GOOGLE);
            String token = jwtService.generateToken(user.getId(), user.getEmail());

            Map<String, Object> data = new HashMap<>();
            data.put("userId", user.getId());
            data.put("email", user.getEmail());
            data.put("username", user.getUsername());
            data.put("token", token);

            return ResponseEntity.ok(new ApiResponse("success", "Đăng nhập Google thành công", data));
        } catch (Exception e) {
            log.error("Lỗi đăng nhập Google: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("error", "Đăng nhập Google thất bại", e.getMessage()));
        }
    }

    @GetMapping("/facebook/callback")
    public ResponseEntity<?> facebookCallback(OAuth2AuthenticationToken authentication) {
        try {
            if (authentication == null) {
                throw new RuntimeException("Không thể xác thực OAuth2");
            }
            OAuth2User oauth2User = authentication.getPrincipal();
            OAuth2UserDetails oauth2UserDetails = new OAuth2FacebookUser(oauth2User.getAttributes());

            // Set createdAt và updatedAt cho user OAuth2
            LocalDateTime now = LocalDateTime.now();
            oauth2UserDetails.setCreatedAt(now);
            oauth2UserDetails.setUpdatedAt(now);

            User user = service.processOAuth2User(oauth2UserDetails, Provider.FACEBOOK);
            String token = jwtService.generateToken(user.getId(), user.getEmail());

            Map<String, Object> data = new HashMap<>();
            data.put("userId", user.getId());
            data.put("email", user.getEmail());
            data.put("username", user.getUsername());
            data.put("token", token);

            return ResponseEntity.ok(new ApiResponse("success", "Đăng nhập Facebook thành công", data));
        } catch (Exception e) {
            log.error("Lỗi đăng nhập Facebook: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("error", "Đăng nhập Facebook thất bại", e.getMessage()));
        }
    }

    @GetMapping("/github/callback")
    public ResponseEntity<?> githubCallback(OAuth2AuthenticationToken authentication) {
        try {
            if (authentication == null) {
                throw new RuntimeException("Không thể xác thực OAuth2");
            }
            OAuth2User oauth2User = authentication.getPrincipal();
            OAuth2UserDetails oauth2UserDetails = new OAuth2GithubUser(oauth2User.getAttributes());

            // Set createdAt và updatedAt cho user OAuth2
            LocalDateTime now = LocalDateTime.now();
            oauth2UserDetails.setCreatedAt(now);
            oauth2UserDetails.setUpdatedAt(now);

            User user = service.processOAuth2User(oauth2UserDetails, Provider.GITHUB);
            String token = jwtService.generateToken(user.getId(), user.getEmail());

            Map<String, Object> data = new HashMap<>();
            data.put("userId", user.getId());
            data.put("email", user.getEmail());
            data.put("username", user.getUsername());
            data.put("token", token);

            return ResponseEntity.ok(new ApiResponse("success", "Đăng nhập GitHub thành công", data));
        } catch (Exception e) {
            log.error("Lỗi đăng nhập Github: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("error", "Đăng nhập GitHub thất bại", e.getMessage()));
        }
    }

    @GetMapping("/login-success")
    public void loginSuccess(OAuth2AuthenticationToken authentication, HttpServletResponse response)
            throws IOException {
        try {
            if (authentication == null) {
                throw new RuntimeException("Không thể xác thực OAuth2");
            }
            OAuth2User oauth2User = authentication.getPrincipal();
            String provider = authentication.getAuthorizedClientRegistrationId();
            OAuth2UserDetails oauth2UserDetails;

            switch (provider.toUpperCase()) {
                case "GOOGLE":
                    oauth2UserDetails = new OAuth2GoogleUser(oauth2User.getAttributes());
                    break;
                case "FACEBOOK":
                    oauth2UserDetails = new OAuth2FacebookUser(oauth2User.getAttributes());
                    break;
                case "GITHUB":
                    oauth2UserDetails = new OAuth2GithubUser(oauth2User.getAttributes());
                    break;
                default:
                    throw new RuntimeException("Provider không được hỗ trợ: " + provider);
            }

            User user = service.processOAuth2User(oauth2UserDetails, Provider.valueOf(provider.toUpperCase()));
            String token = jwtService.generateToken(user.getId(), user.getEmail());

            // Mã hóa các tham số để xử lý các ký tự đặc biệt
            String encodedToken = java.net.URLEncoder.encode(token, "UTF-8");
            String encodedUsername = java.net.URLEncoder.encode(user.getUsername(), "UTF-8");
            String encodedEmail = java.net.URLEncoder.encode(user.getEmail(), "UTF-8");

            // Chuyển hướng đến frontend kèm theo token và thông tin người dùng
            String redirectUrl = String.format(
                    "https://vibely-study-social-website.vercel.app?token=%s&userId=%s&email=%s&username=%s",
                    encodedToken,
                    user.getId(),
                    encodedEmail,
                    encodedUsername);

            response.setStatus(HttpServletResponse.SC_FOUND);
            response.setHeader("Location", redirectUrl);
            response.sendRedirect(redirectUrl);
        } catch (Exception e) {
            log.error("OAuth2 login error: {}", e.getMessage());
            String errorUrl = "https://vibely-study-social-website.vercel.app/user-login?error=" +
                    java.net.URLEncoder.encode(e.getMessage(), "UTF-8");
            response.sendRedirect(errorUrl);
        }
    }

    @DeleteMapping("/deleteAccount")
    public ResponseEntity<ApiResponse> deleteAccount(Authentication authentication) {
        try {
            String email = authentication.getName();
            service.deleteUserByEmail(email);
            return ResponseEntity.ok(new ApiResponse("success", "Xóa tài khoản thành công", null));
        } catch (Exception e) {
            log.error("Xóa tài khoản thất bại: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse("error", "Xóa tài khoản thất bại", e.getMessage()));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            User user = service.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

            // Kiểm tra nếu là tài khoản OAuth
            if (user.getProvider() != Provider.LOCAL) {
                String providerName = user.getProvider().toString().toLowerCase();
                return ResponseEntity.badRequest()
                        .body(new ApiResponse("error",
                                String.format("Không thể đổi mật khẩu vì bạn đăng nhập bằng %s", providerName),
                                null));
            }

            // Đổi mật khẩu
            service.changePassword(email, request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.ok(new ApiResponse("success", "Đổi mật khẩu thành công", null));
        } catch (RuntimeException e) {
            // Xử lý các lỗi cụ thể
            String errorMessage;
            if (e.getMessage().contains("Mật khẩu cũ không đúng")) {
                errorMessage = "Mật khẩu cũ không đúng";
            } else {
                errorMessage = e.getMessage();
            }
            log.error("Đổi mật khẩu thất bại: {}", errorMessage);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("error", errorMessage, null));
        } catch (Exception e) {
            log.error("Đổi mật khẩu thất bại: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("error", "Đổi mật khẩu thất bại", e.getMessage()));
        }
    }
}