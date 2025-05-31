package com.example.vibely_backend.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "google_calendar_tokens")
public class GoogleCalendarToken {
    @Id
    private String id;
    private String userId;
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
    private Long tokenExpiryTime;
    private String scope;
    private String tokenType;
}