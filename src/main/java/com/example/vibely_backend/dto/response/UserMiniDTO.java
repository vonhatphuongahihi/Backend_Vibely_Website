package com.example.vibely_backend.dto.response;

import com.example.vibely_backend.entity.User;
import lombok.Data;

@Data
public class UserMiniDTO {
    private String id;
    private String email;
    private String username;
    private String profilePicture;

    public UserMiniDTO(User user) {
        if (user == null) return; // hoặc throw exception nếu cần

        this.id = user.getId();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.profilePicture = user.getProfilePicture();
    }
}
