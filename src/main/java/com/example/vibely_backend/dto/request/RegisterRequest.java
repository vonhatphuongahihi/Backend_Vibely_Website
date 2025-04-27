package com.example.vibely_backend.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String gender;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime dateOfBirth;

    public LocalDate getDateOfBirthAsLocalDate() {
        if (dateOfBirth != null) {
            return dateOfBirth.toLocalDate();
        }
        return null;
    }
}