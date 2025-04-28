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

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String dateOfBirth;

    public LocalDate getDateOfBirthAsLocalDate() {
        if (dateOfBirth == null) {
            return null;
        }

        try {
            // Thử parse theo định dạng ISO string (có timezone)
            if (dateOfBirth.contains("T")) {
                LocalDateTime dateTime = LocalDateTime.parse(dateOfBirth.substring(0, 19));
                return dateTime.toLocalDate();
            }
            // Nếu không phải ISO string, parse theo định dạng yyyy-MM-dd
            return LocalDate.parse(dateOfBirth);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Định dạng ngày sinh không hợp lệ. Vui lòng sử dụng định dạng yyyy-MM-dd hoặc yyyy-MM-ddTHH:mm:ss.sssZ");
        }
    }
}