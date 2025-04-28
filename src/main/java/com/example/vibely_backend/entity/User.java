package com.example.vibely_backend.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;

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

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
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
    @DBRef
    private List<DocumentUser> savedDocuments = new ArrayList<>();

    private int postsCount = 0;
    private int followerCount = 0;
    private int followingCount = 0;

    @DBRef
    private Bio bio;

    private String verificationCode;
    private LocalDate verificationCodeExpires;
}
