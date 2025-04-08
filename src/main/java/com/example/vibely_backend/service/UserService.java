package com.example.vibely_backend.service;

import com.example.vibely_backend.dto.request.RegisterRequest;
import com.example.vibely_backend.entity.User;
import com.example.vibely_backend.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;

@Slf4j
@Service
public class UserService {

    @Autowired
    private JWTService jwtService;

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    private UserRepository repo;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public User register(User user) {
        log.info("Đăng ký tài khoản: {}", user.getUsername());

        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new RuntimeException("Tên tài khoản là bắt buộc");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new RuntimeException("Mật khẩu là bắt buộc");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Email là bắt buộc");
        }

        if (repo.findByUsername(user.getUsername()).isPresent()) {
            log.warn("Username đã tồn tại: {}", user.getUsername());
            throw new RuntimeException("Username đã tồn tại");
        }
        if (repo.findByEmail(user.getEmail()).isPresent()) {
            log.warn("Email đã tồn tại: {}", user.getEmail());
            throw new RuntimeException("Email đã tồn tại");
        }

        // Initialize collections
        user.setFollowers(new ArrayList<>());
        user.setFollowings(new ArrayList<>());
        user.setPosts(new ArrayList<>());
        user.setLikedPosts(new ArrayList<>());
        user.setSavedPosts(new ArrayList<>());
        user.setSavedDocuments(new ArrayList<>());
        user.setProfilePicture("");
        user.setCoverPicture("");
        user.setPostsCount(0);
        user.setFollowerCount(0);
        user.setFollowingCount(0);
        user.setPassword(encoder.encode(user.getPassword()));

        try {
            User savedUser = repo.save(user);
            log.info("Người dùng đã đăng ký tài khoản thành công: {}", savedUser.getUsername());
            return savedUser;
        } catch (Exception e) {
            log.error("Lỗi đăng ký tài khoản: {}", e.getMessage());
            throw new RuntimeException("Lỗi đăng ký tài khoản: " + e.getMessage());
        }
    }

    public String verify(User user) {
        String usernameOrEmail = user.getUsername() != null ? user.getUsername() : user.getEmail();
        log.info("Đang xác minh tài khoản: {}", usernameOrEmail);

        if (usernameOrEmail == null || usernameOrEmail.trim().isEmpty()) {
            log.warn("Username hoặc email trống");
            throw new RuntimeException("Username hoặc email là bắt buộc");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            log.warn("Mật khẩu trống");
            throw new RuntimeException("Mật khẩu là bắt buộc");
        }

        // Kiểm tra xem tài khoản có tồn tại không
        boolean userExists = repo.findByUsername(usernameOrEmail).isPresent() ||
                repo.findByEmail(usernameOrEmail).isPresent();
        if (!userExists) {
            log.warn("Tài khoản không tồn tại: {}", usernameOrEmail);
            throw new RuntimeException("Tài khoản không tồn tại");
        }

        try {
            log.debug("Bắt đầu xác thực với AuthenticationManager");
            Authentication authentication = authManager
                    .authenticate(new UsernamePasswordAuthenticationToken(usernameOrEmail, user.getPassword()));
            if (authentication.isAuthenticated()) {
                log.info("Tài khoản xác thực thành công: {}", usernameOrEmail);
                return jwtService.generateToken(usernameOrEmail);
            }
            log.warn("Tài khoản xác thực thất bại: {}", usernameOrEmail);
            throw new RuntimeException("Xác thực thất bại");
        } catch (AuthenticationException e) {
            log.error("Lỗi xác thực tài khoản {}: {}", usernameOrEmail, e.getMessage());
            throw new RuntimeException("Username/email hoặc password sai");
        }
    }

    public String generateToken(User user) {
        return jwtService.generateToken(user.getUsername());
    }
}