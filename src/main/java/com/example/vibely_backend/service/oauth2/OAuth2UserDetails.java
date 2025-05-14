package com.example.vibely_backend.service.oauth2;

import java.util.Map;

public interface OAuth2UserDetails {
    String getId();

    String getName();

    String getEmail();

    String getImageUrl();

    Map<String, Object> getAttributes();
}
