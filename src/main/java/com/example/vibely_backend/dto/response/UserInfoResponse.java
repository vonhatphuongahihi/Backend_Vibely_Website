package com.example.vibely_backend.dto.response;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {
    private String id;
    private String username;
    private String email;
    private String gender;
    private LocalDate dateOfBirth;
    private String profilePicture;
    private String coverPicture;

    private List<String> followers;
    private List<String> followings;
    private List<String> posts;
    private List<String> likedPosts;
    private List<String> savedPosts;
    private List<String> savedDocuments;

    private int postsCount;
    private int followerCount;
    private int followingCount;

    private BioResponse bio;
}
