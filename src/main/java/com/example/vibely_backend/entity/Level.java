package com.example.vibely_backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "levels")
public class Level {

    @Id
    private String id;

    private String name;
    private String description;

    private Date createdAt;
    private Date updatedAt;
}
