package com.example.vibely_backend.controller;

import com.example.vibely_backend.dto.response.ApiResponse;
import com.example.vibely_backend.entity.User;
import com.example.vibely_backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = { "http://localhost:3001", "http://localhost:3000", "http://127.0.0.1:3001",
        "http://127.0.0.1:3000" })
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/check-auth")
    public ResponseEntity<?> checkAuth() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()
                    && !"anonymousUser".equals(authentication.getPrincipal())) {
                String email = authentication.getName();
                return userService.findByEmail(email)
                        .map(user -> ResponseEntity.ok(new ApiResponse("success", "User is authenticated", user)))
                        .orElse(ResponseEntity.ok(new ApiResponse("error", "User not found", null)));
            }
            return ResponseEntity.ok(new ApiResponse("error", "User is not authenticated", null));
        } catch (Exception e) {
            logger.error("Error checking authentication status", e);
            return ResponseEntity.ok(new ApiResponse("error", "Error checking authentication status", null));
        }
    }
}