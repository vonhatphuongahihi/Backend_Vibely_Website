package com.example.vibely_backend.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;
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

    @Field("username")
    private String username;

    @Field("email")
    private String email;

    @JsonIgnore
    @Field("password")
    private String password;

    @Field("gender")
    private String gender;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Field("date_of_birth")
    private LocalDate dateOfBirth;

    @Field("profile_picture")
    private String profilePicture;

    @Field("cover_picture")
    private String coverPicture;

    @Field("followers")
    private List<String> followers = new ArrayList<>();

    @Field("followings")
    private List<String> followings = new ArrayList<>();

    @Field("posts")
    private List<String> posts = new ArrayList<>();

    @Field("liked_posts")
    private List<String> likedPosts = new ArrayList<>();

    @Field("saved_posts")
    private List<String> savedPosts = new ArrayList<>();

    @Field("saved_documents")
    private List<String> savedDocuments = new ArrayList<>();

    @Field("posts_count")
    private int postsCount = 0;

    @Field("follower_count")
    private int followerCount = 0;

    @Field("following_count")
    private int followingCount = 0;

    @Field("verification_code")
    private String verificationCode;

    @Field("verification_code_expires")
    private LocalDate verificationCodeExpires;

    @Field("name")
    private String name;

    @Field("image_url")
    private String imageUrl;

    @Field("enabled")
    private boolean enabled = true;

    @Field("provider")
    private Provider provider = Provider.LOCAL;

    @Field("role")
    private String role = "ROLE_USER";

    // Google Calendar fields
    @Field("google_calendar_access_token")
    private String googleCalendarAccessToken;

    @Field("google_calendar_refresh_token")
    private String googleCalendarRefreshToken;

    @Field("google_calendar_token_expiry")
    private LocalDateTime googleCalendarTokenExpiry;

    @Field("google_calendar_connected")
    private boolean googleCalendarConnected = false;

    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Field("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Field("updated_at")
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
