package com.example.vibely_backend.dto.request;

import lombok.Data;

@Data
public class DocumentRequest {
    private String title;
    private int pages;
    private String fileType;
    private String fileUrl;
    private String levelId;
    private String subjectId;
}
