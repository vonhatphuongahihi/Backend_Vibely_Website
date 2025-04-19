package com.example.vibely_backend.controller;

import com.example.vibely_backend.entity.User;
import com.example.vibely_backend.entity.Post;
import com.example.vibely_backend.repository.UserRepository;
import com.example.vibely_backend.repository.PostRepository;
import com.example.vibely_backend.dto.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class AdminUserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    // Lấy danh sách tất cả users
    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        try {
            List<User> users = userRepository.findAll().stream()
                    .peek(u -> u.setPassword(null)) // Ẩn password
                    .collect(Collectors.toList());
    
            ApiResponse<List<User>> response = new ApiResponse<>(200, "Lấy danh sách người dùng thành công", users);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<String> errorResponse = new ApiResponse<>(500, "Lỗi khi lấy danh sách người dùng", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    

    // Xóa user theo ID
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(404, "Không tìm thấy người dùng", null));
            }

            // Xóa tất cả bài viết của user
            postRepository.deleteByAuthor(userId);

            // Xóa user
            userRepository.deleteById(userId);

            return ResponseEntity.ok(new ApiResponse(200, "Xóa người dùng thành công", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(500, "Lỗi khi xóa người dùng", e.getMessage()));
        }
    }

    // Tìm kiếm users
    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(@RequestParam(required = false) String q) {
        try {
            if (q == null || q.isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse(400, "Vui lòng nhập từ khóa tìm kiếm", null));
            }

            List<User> users = userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(q, q).stream()
                    .peek(u -> u.setPassword(null)) // Ẩn password
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new ApiResponse(200, "Tìm kiếm người dùng thành công", users));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(500, "Lỗi khi tìm kiếm người dùng", e.getMessage()));
        }
    }
}
