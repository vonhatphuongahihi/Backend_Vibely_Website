package com.example.vibely_backend.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
    @JsonIgnore
    private Bio bio = null;

    private String verificationCode;
    private LocalDate verificationCodeExpires;

    private String name;
    private String imageUrl;
    private boolean enabled = true;
    private Provider provider = Provider.LOCAL;
    private String role = "ROLE_USER";

    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{id='" + id + "', username='" + username + "', email='" + email + "'}";
    }
}