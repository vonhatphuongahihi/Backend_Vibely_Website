package com.example.vibely_backend.service.oauth2;

import java.util.Map;

public class OAuth2GoogleUser implements OAuth2UserDetails {
    private Map<String, Object> attributes;

    public OAuth2GoogleUser(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("picture");
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
