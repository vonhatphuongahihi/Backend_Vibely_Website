package com.example.vibely_backend.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.vibely_backend.entity.Provider;
import com.example.vibely_backend.entity.User;
import com.example.vibely_backend.repository.UserRepository;
import com.example.vibely_backend.service.EmailService;

@RestController
@RequestMapping("/forgot-password")
public class ForgotPasswordController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    private String generateVerificationCode() {
        return String.valueOf((int) (100000 + Math.random() * 900000));
    }

    @PostMapping("/send-code")
    public ResponseEntity<?> sendCode(@RequestBody Map<String, String> request) {
        Map<String, String> response = new HashMap<>();
        try {
            String email = request.get("email");
            Optional<User> optionalUser = userRepository.findByEmailAndProvider(email, Provider.LOCAL);

            if (optionalUser.isEmpty()) {
                response.put("message", "Không tìm thấy tài khoản với email này");
                return ResponseEntity.status(404).body(response);
            }

            User user = optionalUser.get();
            String code = generateVerificationCode();
            user.setVerificationCode(code);

            // Đặt thời hạn hết hạn là ngày hôm nay + 1 (hoặc theo thời gian cụ thể hơn)
            user.setVerificationCodeExpires(LocalDate.now().plusDays(1)); // hết hạn sau 1 ngày
            userRepository.save(user);

            emailService.sendVerificationCode(email, code);

            response.put("message", "Đã gửi mã xác thực qua email");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Đã xảy ra lỗi: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody Map<String, String> request) {
        Map<String, String> response = new HashMap<>();
        try {
            String email = request.get("email");
            String code = request.get("code");

            Optional<User> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isEmpty()) {
                response.put("message", "Không tìm thấy tài khoản");
                return ResponseEntity.status(404).body(response);
            }

            User user = optionalUser.get();
            if (!code.equals(user.getVerificationCode())) {
                response.put("message", "Mã xác thực không chính xác");
                return ResponseEntity.badRequest().body(response);
            }

            // So sánh thời gian hiện tại với thời gian hết hạn
            if (LocalDate.now().isAfter(user.getVerificationCodeExpires())) {
                response.put("message", "Mã xác thực đã hết hạn");
                return ResponseEntity.badRequest().body(response);
            }

            response.put("message", "Xác thực thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Đã xảy ra lỗi: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        Map<String, String> response = new HashMap<>();
        try {
            String email = request.get("email");
            String code = request.get("code");
            String newPassword = request.get("newPassword");

            Optional<User> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isEmpty()) {
                response.put("message", "Không tìm thấy tài khoản");
                return ResponseEntity.status(404).body(response);
            }

            User user = optionalUser.get();
            if (!code.equals(user.getVerificationCode())) {
                response.put("message", "Mã xác thực không chính xác");
                return ResponseEntity.badRequest().body(response);
            }

            if (LocalDate.now().isAfter(user.getVerificationCodeExpires())) {
                response.put("message", "Mã xác thực đã hết hạn");
                return ResponseEntity.badRequest().body(response);
            }

            String hashedPassword = new BCryptPasswordEncoder().encode(newPassword);
            user.setPassword(hashedPassword);
            user.setVerificationCode(null);
            user.setVerificationCodeExpires(null);
            userRepository.save(user);

            response.put("message", "Đặt lại mật khẩu thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Đã xảy ra lỗi: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
