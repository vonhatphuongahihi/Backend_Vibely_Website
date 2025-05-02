package com.example.vibely_backend.service;

import com.example.vibely_backend.entity.Admin;
import com.example.vibely_backend.repository.AdminRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private AuthenticationManager authManager;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public Admin register(Admin admin) {

        if (admin.getUsername() == null || admin.getUsername().trim().isEmpty()) {
            throw new RuntimeException("Tên tài khoản là bắt buộc");
        }
        if (admin.getPassword() == null || admin.getPassword().trim().isEmpty()) {
            throw new RuntimeException("Mật khẩu là bắt buộc");
        }
        if (admin.getEmail() == null || admin.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Email là bắt buộc");
        }

        if (adminRepository.findByUsername(admin.getUsername()).isPresent()) {
            log.warn("Username admin đã tồn tại: {}", admin.getUsername());
            throw new RuntimeException("Username đã tồn tại");
        }
        if (adminRepository.findByEmail(admin.getEmail()).isPresent()) {
            log.warn("Email admin đã tồn tại: {}", admin.getEmail());
            throw new RuntimeException("Email đã tồn tại");
        }

        // Thiết lập mật khẩu được mã hóa
        admin.setPassword(encoder.encode(admin.getPassword()));

        try {
            Admin savedAdmin = adminRepository.save(admin);
            return savedAdmin;
        } catch (Exception e) {
            log.error("Lỗi đăng ký admin: {}", e.getMessage());
            throw new RuntimeException("Lỗi đăng ký admin: " + e.getMessage());
        }
    }

    public String verify(Admin admin) {
        String usernameOrEmail = admin.getUsername() != null ? admin.getUsername() : admin.getEmail();

        if (usernameOrEmail == null || usernameOrEmail.trim().isEmpty()) {
            throw new RuntimeException("Username hoặc email là bắt buộc");
        }
        if (admin.getPassword() == null || admin.getPassword().trim().isEmpty()) {
            throw new RuntimeException("Mật khẩu là bắt buộc");
        }

        boolean exists = adminRepository.findByUsername(usernameOrEmail).isPresent() ||
                adminRepository.findByEmail(usernameOrEmail).isPresent();

        if (!exists) {
            log.warn("Admin không tồn tại: {}", usernameOrEmail);
            throw new RuntimeException("Tài khoản không tồn tại");
        }

        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(usernameOrEmail, admin.getPassword()));

            if (authentication.isAuthenticated()) {
                return jwtService.generateToken(usernameOrEmail);
            }

            throw new RuntimeException("Xác thực thất bại");
        } catch (AuthenticationException e) {
            log.error("Lỗi xác thực admin {}: {}", usernameOrEmail, e.getMessage());
            throw new RuntimeException("Username/email hoặc password sai");
        }
    }

    public String generateToken(Admin admin) {
        return jwtService.generateToken(admin.getUsername());
    }

    public void updatePassword(String email, String oldPassword, String newPassword) {
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy admin: " + email));

        if (!passwordEncoder.matches(oldPassword, admin.getPassword())) {
            throw new RuntimeException("Mật khẩu cũ không đúng");
        }

        String hashedPassword = passwordEncoder.encode(newPassword);
        admin.setPassword(hashedPassword);

        adminRepository.save(admin);
    }
}
