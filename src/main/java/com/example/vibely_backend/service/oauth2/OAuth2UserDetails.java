package com.example.vibely_backend.service.oauth2;

import java.time.LocalDateTime;
import java.util.Map;

public interface OAuth2UserDetails {
    String getId();

    String getName();

    String getEmail();

    String getImageUrl();

    Map<String, Object> getAttributes();

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();

    void setCreatedAt(LocalDateTime createdAt);

    void setUpdatedAt(LocalDateTime updatedAt);
}
