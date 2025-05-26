package com.example.vibely_backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "schedules")
public class Schedule {
    @Id
    private String id;

    @Field("user")
    private String userId;

    @Field("subject")
    private String subject;

    @Field("startTime")
    private LocalDateTime startTime;

    @Field("endTime")
    private LocalDateTime endTime;

    @Field("categoryColor")
    private String categoryColor = "#0000FF";

    @Field("googleCalendarEventId")
    private String googleCalendarEventId;

    @CreatedDate
    @Field("createdAt")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("updatedAt")
    private LocalDateTime updatedAt;
}