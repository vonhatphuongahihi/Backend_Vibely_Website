package com.example.vibely_backend.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {
    private String id;
    private String title;
    private Integer pages;
    private String fileType;
    private String fileUrl;

    private String levelId;
    private String levelName;

    private String subjectId;
    private String subjectName;

    private LocalDateTime uploadDate;
}
