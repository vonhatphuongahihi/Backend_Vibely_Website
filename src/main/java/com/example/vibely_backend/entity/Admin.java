package com.example.vibely_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "admin")
public class Admin {
    @Id
    private String id;

    @Field("firstName")
    private String firstName;

    @Field("lastName")
    private String lastName;

    @Field("username")
    private String username;

    @Field("email")
    private String email;

    @Field("phone")
    private String phone;

    @JsonIgnore
    @Field("password")
    private String password;

    @Field("nationality")
    private String nationality;

    @Field("city")
    private String city;

    @Field("profilePicture")
    private String profilePicture;

    @Field("role")
    private String role = "admin";

    @Field("createdAt")
    private LocalDateTime createdAt;

    @Field("updatedAt")
    private LocalDateTime updatedAt;

    public void setRole(String role) {
        this.role = role;
    }
}
