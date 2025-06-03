package com.example.vibely_backend.controller;

import com.example.vibely_backend.entity.User;
import com.example.vibely_backend.entity.Post;
import com.example.vibely_backend.repository.UserRepository;
import com.example.vibely_backend.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @GetMapping("")
    public Map<String, Object> getAllUsers() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<User> users = userRepository.findAll();
            users.forEach(u -> u.setPassword(null)); // ẩn password

            response.put("status", "success");
            response.put("data", users);
            return response;
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Lỗi khi lấy danh sách người dùng");
            response.put("error", e.getMessage());
            return response;
        }
    }

    @DeleteMapping("/{userId}")
    public Map<String, Object> deleteUser(@PathVariable String userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<User> optionalUser = userRepository.findById(userId);
            if (!optionalUser.isPresent()) {
                response.put("status", "error");
                response.put("message", "Không tìm thấy người dùng");
                return response;
            }

            User user = optionalUser.get();

            // Xóa tất cả bài viết của user
            postRepository.deleteByUserId(userId);

            userRepository.deleteById(userId);

            response.put("status", "success");
            response.put("message", "Xóa người dùng thành công");
            return response;
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Lỗi khi xóa người dùng");
            response.put("error", e.getMessage());
            return response;
        }
    }

    @GetMapping("/search")
    public Map<String, Object> searchUsers(@RequestParam String q) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (q == null || q.trim().isEmpty()) {
                response.put("status", "error");
                response.put("message", "Vui lòng nhập từ khóa tìm kiếm");
                return response;
            }

            List<User> users = userRepository
                    .findByUsernameIgnoreCaseContainingOrEmailIgnoreCaseContaining(q, q);
            users.forEach(u -> u.setPassword(null)); // ẩn password

            response.put("status", "success");
            response.put("data", users);
            return response;
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Lỗi khi tìm kiếm người dùng");
            response.put("error", e.getMessage());
            return response;
        }
    }

    @GetMapping("/{userId}/friends")
    public Map<String, Object> getAllFriends(@PathVariable String userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<User> optionalUser = userRepository.findById(userId);
            if (!optionalUser.isPresent()) {
                response.put("status", "error");
                response.put("message", "Người dùng không tồn tại");
                return response;
            }
            User user = optionalUser.get();

            // Lấy danh sách ID người dùng đang theo dõi
            Set<String> followingIds = new HashSet<>(user.getFollowings());

            // Lọc ra những người dùng vừa là follower vừa là following
            List<User> mutualFriends = userRepository.findAllById(
                    user.getFollowers().stream()
                            .filter(followerId -> followingIds.contains(followerId))
                            .collect(Collectors.toList()));

            response.put("status", "success");
            response.put("message", "Lấy danh sách bạn chung thành công");
            response.put("data", mutualFriends);
            return response;
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Lỗi khi lấy danh sách bạn chung");
            response.put("error", e.getMessage());
            return response;
        }
    }
}