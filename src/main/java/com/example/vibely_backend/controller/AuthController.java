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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import com.example.vibely_backend.service.JWTService;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService service;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTService jwtService;

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
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest loginRequest) {
        log.debug("Bắt đầu xác thực với AuthenticationManager");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        log.info("Xác thực thành công cho người dùng: {}", loginRequest.getEmail());

        // Tạo token
        String token = jwtService.generateToken(loginRequest.getEmail());
        log.debug("Đã xác thực người dùng và tạo token: {}", token);

        // Lấy thông tin người dùng
        User user = service.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Tạo response
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId()); // Sử dụng _id của user
        data.put("email", user.getEmail());
        data.put("username", user.getUsername());
        data.put("token", token);

        return ResponseEntity.ok(new ApiResponse(200, "Đăng nhập thành công", data));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpServletResponse response) {
        // Xóa cookie bằng cách đặt maxAge = 0
        Cookie cookie = new Cookie("auth_token", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(0); // hết hạn ngay lập tức
        response.addCookie(cookie);

        return ResponseEntity.ok(new ApiResponse(200, "Đăng xuất thành công", null));
    }

}
