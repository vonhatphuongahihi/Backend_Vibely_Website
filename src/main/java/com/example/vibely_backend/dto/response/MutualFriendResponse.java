package com.example.vibely_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MutualFriendResponse {
    private String id;
    private String username;
    private String profilePicture;
    private String email;
    private int followerCount;
    private int followingCount;
}
