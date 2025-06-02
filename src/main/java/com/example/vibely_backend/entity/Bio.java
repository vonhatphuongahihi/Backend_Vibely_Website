package com.example.vibely_backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "bios")
public class Bio {

    @Id
    private String id;
    private String bioText;
    private String liveIn;
    private String relationship;
    private String workplace;
    private String education;
    // private String phone;
    private String hometown;

    @JsonIgnore
    private String userId;

    private Date createdAt;
    private Date updatedAt;
}
