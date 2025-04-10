package com.example.vibely_backend.mapper;

import com.example.vibely_backend.entity.User;

import java.time.LocalDate;


import org.springframework.stereotype.Component;

@Component
public class UserPrincipalMapper {
    public User toUserPrincipal(User user) {
        // Trả về đối tượng User trực tiếp
        return user; // Nếu bạn chỉ cần User
    }

    public User createUser(String id, String username, String email, String password, 
    String gender, LocalDate dateOfBirth, String profilePicture, 
    String coverPicture) {
    User user = new User();
    user.setId(id);  // Thiết lập ID cho người dùng mới
    user.setUsername(username);
    user.setEmail(email);
    user.setPassword(password);
    user.setGender(gender);  // Thiết lập giới tính
    user.setDateOfBirth(dateOfBirth);  // Thiết lập ngày sinh
    user.setProfilePicture(profilePicture);  // Thiết lập ảnh đại diện
    user.setCoverPicture(coverPicture);  // Thiết lập ảnh bìa
    user.setPostsCount(0);  // Khởi tạo số bài viết
    user.setFollowerCount(0);  // Khởi tạo số người theo dõi
    user.setFollowingCount(0);  // Khởi tạo số người mà người dùng đang theo dõi
    user.setVerificationCode(null);  // Có thể thiết lập mã xác minh nếu cần
    user.setVerificationCodeExpires(null);  // Thời gian hết hạn mã xác minh

    return user;  // Trả về đối tượng User đã tạo
    }

}