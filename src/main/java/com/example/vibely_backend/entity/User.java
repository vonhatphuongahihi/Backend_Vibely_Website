package com.example.vibely_backend.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;
    private String username;
    private String email;
    private String password;
    private String gender;
    private Date dateOfBirth;
    private String profilePicture;
    private String coverPicture;

    // Dùng @DBRef và load trực tiếp (không lazy)
    @DBRef(lazy = false)
    private List<User> followers = new ArrayList<>();

    @DBRef(lazy = false)
    private List<User> followings = new ArrayList<>();

    @DBRef
    private List<Post> posts = new ArrayList<>();

    @DBRef
    private List<Post> likedPosts = new ArrayList<>();

    @DBRef
    private List<Post> savedPosts = new ArrayList<>();

    private int postsCount = 0;
    private int followerCount = 0;
    private int followingCount = 0;

    @DBRef(lazy = true)
    private Bio bio;

    @DBRef(lazy = true)
    private List<UserDocument> savedDocuments = new ArrayList<>();

    private String verificationCode;
    private Date verificationCodeExpires;
}
