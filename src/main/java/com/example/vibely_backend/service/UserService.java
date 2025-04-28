package com.example.vibely_backend.service;

import com.example.vibely_backend.dto.request.UserProfileUpdateRequest;
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
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    @Autowired
    private JWTService jwtService;

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    private UserRepository userRepository;

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

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            log.warn("Username đã tồn tại: {}", user.getUsername());
            throw new RuntimeException("Username đã tồn tại");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
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
            User savedUser = userRepository.save(user);
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

        try {
            log.debug("Bắt đầu xác thực với AuthenticationManager");
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(usernameOrEmail, user.getPassword()));

            if (authentication.isAuthenticated()) {
                log.info("Xác thực thành công cho người dùng: {}", usernameOrEmail);
                return jwtService.generateToken(usernameOrEmail);
            } else {
                log.warn("Xác thực thất bại cho người dùng: {}", usernameOrEmail);
                throw new RuntimeException("Xác thực thất bại");
            }
        } catch (AuthenticationException e) {
            log.warn("Lỗi xác thực: {}", e.getMessage());
            throw new RuntimeException("Username hoặc password không đúng");
        } catch (Exception e) {
            log.error("Lỗi không xác định trong quá trình xác thực: {}", e.getMessage());
            throw new RuntimeException("Lỗi xác thực: " + e.getMessage());
        }
    }

    public String generateToken(User user) {
        return jwtService.generateToken(user.getUsername());
    }

    public Optional<User> findByEmail(String email) {
        log.debug("Tìm user với email: {}", email);
        return userRepository.findByEmail(email);
    }

    public User updateUserProfile(
            String userId,
            UserProfileUpdateRequest request,
            String profilePictureUrl,
            String coverPictureUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Cập nhật thông tin từ request
        if (request.getUsername() != null)
            user.setUsername(request.getUsername());
        if (request.getEmail() != null)
            user.setEmail(request.getEmail());
        if (request.getGender() != null)
            user.setGender(request.getGender());

        // Nếu dateOfBirth là String thì cần parse:
        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }

        // Cập nhật ảnh nếu có
        if (profilePictureUrl != null)
            user.setProfilePicture(profilePictureUrl);
        if (coverPictureUrl != null)
            user.setCoverPicture(coverPictureUrl);

        return userRepository.save(user);
    }
}