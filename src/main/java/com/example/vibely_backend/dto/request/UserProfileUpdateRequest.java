package com.example.vibely_backend.dto.request;

import lombok.Data;
import java.time.LocalDate;


@Data
public class UserProfileUpdateRequest {
    private String username;
    private String email;
    private String gender;
    private LocalDate dateOfBirth;
}
