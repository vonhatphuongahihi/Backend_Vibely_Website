package com.example.vibely_backend.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "documents")
public class DocumentUser {
    @Id
    private String id;

    @Field("title")
    private String title;

    @Field("pages")
    private Integer pages;

    @Field("level_id")
    private String levelId;

    @Field("subject_id")
    private String subjectId;

    @Field("file_type")
    private String fileType;

    @Field("file_url")
    private String fileUrl;

    @Field("upload_date")
    private LocalDateTime uploadDate;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;
}
