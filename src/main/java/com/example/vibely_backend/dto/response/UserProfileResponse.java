package com.example.vibely_backend.dto.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private String id;
    private String username;
    private String email;
    private String gender;
    private LocalDate dateOfBirth;
    private String profilePicture;
    private String coverPicture;
    private BioResponse bio;
}