package com.example.vibely_backend.controller;

import com.example.vibely_backend.service.OAuth2Service;
import com.example.vibely_backend.service.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class OAuth2Controller {

    private final OAuth2Service oauth2Service;
    private final JWTService jwtService;

    @GetMapping("/facebook/callback")
    public String facebookCallback(@AuthenticationPrincipal OAuth2User oauth2User) {
        var user = oauth2Service.processOAuth2User(oauth2User);
        return jwtService.generateToken(user.getId(), user.getEmail());
    }

    @GetMapping("/google/callback")
    public String googleCallback(@AuthenticationPrincipal OAuth2User oauth2User) {
        var user = oauth2Service.processOAuth2User(oauth2User);
        return jwtService.generateToken(user.getId(), user.getEmail());
    }
}