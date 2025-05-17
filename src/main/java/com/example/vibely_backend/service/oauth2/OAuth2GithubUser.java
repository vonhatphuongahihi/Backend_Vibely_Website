package com.example.vibely_backend.service.oauth2;

import java.time.LocalDateTime;
import java.util.Map;

public class OAuth2GithubUser implements OAuth2UserDetails {
    private Map<String, Object> attributes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public OAuth2GithubUser(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getId() {
        Object id = attributes.get("id");
        if (id == null) {
            return "";
        }
        return id.toString();
    }

    @Override
    public String getName() {
        String name = (String) attributes.get("name");
        if (name == null || name.isEmpty()) {
            name = (String) attributes.get("login");
        }
        return name != null ? name : "";
    }

    @Override
    public String getEmail() {
        String email = (String) attributes.get("email");
        if (email == null || email.isEmpty()) {
            Object emails = attributes.get("emails");
            if (emails instanceof java.util.List && !((java.util.List<?>) emails).isEmpty()) {
                Object firstEmail = ((java.util.List<?>) emails).get(0);
                if (firstEmail instanceof Map) {
                    email = (String) ((Map<?, ?>) firstEmail).get("email");
                }
            }
        }
        return email != null ? email : "";
    }

    @Override
    public String getImageUrl() {
        String avatarUrl = (String) attributes.get("avatar_url");
        return avatarUrl != null ? avatarUrl : "";
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
