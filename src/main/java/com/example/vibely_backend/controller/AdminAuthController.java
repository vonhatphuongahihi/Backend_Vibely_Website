package com.example.vibely_backend.controller;

import com.example.vibely_backend.dto.request.LoginRequest;
import com.example.vibely_backend.dto.request.RegisterAdminRequest;
import com.example.vibely_backend.entity.Admin;
import com.example.vibely_backend.repository.AdminRepository;
import com.example.vibely_backend.entity.AdminPrincipal;
import com.example.vibely_backend.service.MyAdminDetailsService;
import com.example.vibely_backend.service.JWTService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = { "http://localhost:3001", "http://localhost:3000", "http://127.0.0.1:3001",
        "http://127.0.0.1:3000" }, allowCredentials = "true")
public class AdminAuthController {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final MyAdminDetailsService adminDetailsService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterAdminRequest request, HttpServletResponse response) {
        try {
            response.setContentType("application/json;charset=UTF-8");

            // Kiểm tra các trường bắt buộc
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                log.warn("Username trống");
                return ResponseEntity.badRequest().body("Username là bắt buộc");
            }
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                log.warn("Email trống");
                return ResponseEntity.badRequest().body("Email là bắt buộc");
            }
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                log.warn("Password trống");
                return ResponseEntity.badRequest().body("Password là bắt buộc");
            }

            // Kiểm tra username và email đã tồn tại
            if (adminRepository.existsByUsername(request.getUsername())) {
                log.warn("Username đã tồn tại: {}", request.getUsername());
                return ResponseEntity.badRequest().body("Username đã tồn tại");
            }
            if (adminRepository.existsByEmail(request.getEmail())) {
                log.warn("Email đã tồn tại: {}", request.getEmail());
                return ResponseEntity.badRequest().body("Email đã tồn tại");
            }

            // Tạo admin mới
            Admin admin = new Admin();
            admin.setFirstName(request.getFirstName());
            admin.setLastName(request.getLastName());
            admin.setUsername(request.getUsername());
            admin.setEmail(request.getEmail());
            admin.setPhone(request.getPhone());
            admin.setPassword(passwordEncoder.encode(request.getPassword()));
            admin.setNationality(request.getNationality());
            admin.setCity(request.getCity());
            admin.setRole("admin");

            // Gán thời gian tạo và cập nhật
            LocalDateTime now = LocalDateTime.now();
            admin.setCreatedAt(now);
            admin.setUpdatedAt(now);

            // Gán ảnh đại diện nếu có
            admin.setProfilePicture(request.getProfilePicture());

            // Lưu admin vào database
            adminRepository.save(admin);

            // Tạo token
            String token = jwtService.generateToken(admin.getUsername());

            // Tạo cookie
            Cookie cookie = new Cookie("token", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(24 * 60 * 60); // 24 giờ
            response.addCookie(cookie);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("message", "Đăng ký thành công");
            responseBody.put("token", token);
            responseBody.put("admin", admin);

            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            log.error("Lỗi khi đăng ký admin: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra khi đăng ký");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        try {
            response.setContentType("application/json;charset=UTF-8");

            // Kiểm tra email và password
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Email là bắt buộc");
            }
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Password là bắt buộc");
            }

            // Xác thực
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Tạo token
            String token = jwtService.generateToken(request.getEmail());

            // Tạo cookie
            Cookie cookie = new Cookie("token", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(24 * 60 * 60);
            response.addCookie(cookie);

            // Lấy thông tin admin
            Admin admin = adminRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Admin không tồn tại"));

            // Tạo response body đơn giản hơn
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("success", true);
            responseBody.put("message", "Đăng nhập thành công");
            responseBody.put("token", token);
            responseBody.put("admin", admin);

            return ResponseEntity.ok(responseBody);
        } catch (BadCredentialsException e) {
            log.warn("Đăng nhập thất bại: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Email hoặc password không đúng");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        } catch (Exception e) {
            log.error("Lỗi khi đăng nhập: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Có lỗi xảy ra khi đăng nhập");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        try {
            response.setContentType("application/json;charset=UTF-8");

            // Xóa cookie
            Cookie cookie = new Cookie("token", null);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);

            // Xóa authentication
            SecurityContextHolder.clearContext();

            return ResponseEntity.ok("Đăng xuất thành công");
        } catch (Exception e) {
            log.error("Lỗi khi đăng xuất: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra khi đăng xuất");
        }
    }
}
