package com.example.vibely_backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @DBRef
    private List<User> followers = new ArrayList<>();

    @DBRef
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

    @DBRef
    private Bio bio;

    @DBRef
    private List<UserDocument> savedDocuments = new ArrayList<>();

    private String verificationCode;
    private Date verificationCodeExpires;
}
