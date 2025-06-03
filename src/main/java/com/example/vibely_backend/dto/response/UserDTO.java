package com.example.vibely_backend.dto.response;

import com.example.vibely_backend.entity.User;
import lombok.Data;
import java.time.LocalDate;

@Data
public class UserDTO {
    private String id;
    private String username;
    private String email;
    private String gender;
    private LocalDate dateOfBirth;
    private String profilePicture;
    private String coverPicture;
    private int postsCount;
    private int followerCount;
    private int followingCount;
    private BioResponse bio;

    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.gender = user.getGender();
        this.dateOfBirth = user.getDateOfBirth();
        this.profilePicture = user.getProfilePicture();
        this.coverPicture = user.getCoverPicture();
        this.postsCount = user.getPostsCount();
        this.followerCount = user.getFollowerCount();
        this.followingCount = user.getFollowingCount();
    }
}
