package com.example.vibely_backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "documents")
public class UserDocument {

    @Id
    private String id;

    private String title;
    private int pages;

    @DBRef
    private Level level;

    @DBRef
    private Subject subject;

    private String fileType;
    private Date uploadDate;
    private String fileUrl;

    private Date createdAt;
    private Date updatedAt;
}
