package com.example.vibely_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.vibely_backend.entity.User;
import com.example.vibely_backend.service.UserService;
import com.example.vibely_backend.dto.request.RegisterRequest;
import com.example.vibely_backend.dto.request.LoginRequest;
import com.example.vibely_backend.dto.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService service;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody RegisterRequest registerRequest,
            HttpServletResponse response) {
        try {
            log.info("Nhận yêu cầu đăng ký từ người dùng: {}", registerRequest.getUsername());

            // Chuyển đổi RegisterRequest thành User
            User user = new User();
            user.setUsername(registerRequest.getUsername());
            user.setEmail(registerRequest.getEmail());
            user.setPassword(registerRequest.getPassword());
            user.setGender(registerRequest.getGender());
            user.setDateOfBirth(registerRequest.getDateOfBirth());

            log.debug("Đang chuyển đổi RegisterRequest thành User: {}", user);

            User registeredUser = service.register(user);
            log.info("Đăng ký người dùng thành công: {}", registeredUser.getUsername());

            // Tạo token JWT
            String token = service.generateToken(registeredUser);
            log.debug("Đã tạo token JWT cho người dùng: {}", registeredUser.getUsername());

            // Thiết lập cookie
            Cookie cookie = new Cookie("auth_token", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(true); // cho HTTPS
            cookie.setPath("/");
            response.addCookie(cookie);

            // Tạo dữ liệu phản hồi
            Map<String, Object> userData = new HashMap<>();
            userData.put("username", registeredUser.getUsername());
            userData.put("email", registeredUser.getEmail());

            return ResponseEntity.status(201).body(new ApiResponse(201, "Đăng ký thành công", userData));
        } catch (RuntimeException e) {
            log.error("Đăng ký thất bại: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(400, "Đăng ký thất bại", e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi không xác định trong quá trình đăng ký: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(new ApiResponse(500, "Lỗi hệ thống", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try {
            log.info("Yêu cầu đăng nhập từ người dùng: {}", loginRequest.getUsername());

            // Chuyển đổi LoginRequest thành User
            User user = new User();
            user.setUsername(loginRequest.getUsername());
            user.setEmail(loginRequest.getEmail());
            user.setPassword(loginRequest.getPassword());

            String token = service.verify(user);
            log.debug("Đã xác thực người dùng và tạo token: {}", user.getUsername());

            // Thiết lập cookie
            Cookie cookie = new Cookie("auth_token", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            response.addCookie(cookie);

            Map<String, String> userData = new HashMap<>();
            userData.put("username", user.getUsername());
            userData.put("email", user.getEmail());

            return ResponseEntity.ok(new ApiResponse(200, "Đăng nhập thành công", userData));
        } catch (RuntimeException e) {
            log.error("Đăng nhập thất bại: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse(400, "Đăng nhập thất bại", e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi không xác định trong quá trình đăng nhập: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(new ApiResponse(500, "Lỗi hệ thống", e.getMessage()));
        }
    }
}
