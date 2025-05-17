package com.example.vibely_backend.service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import com.example.vibely_backend.service.oauth2.OAuth2UserDetails;
import com.example.vibely_backend.service.oauth2.OAuth2GoogleUser;
import com.example.vibely_backend.service.oauth2.OAuth2FacebookUser;
import com.example.vibely_backend.service.oauth2.OAuth2GithubUser;
import com.example.vibely_backend.entity.Provider;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collections;

@Slf4j
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        try {
            OAuth2User oauth2User = super.loadUser(userRequest);
            String provider = userRequest.getClientRegistration().getRegistrationId();
            OAuth2UserDetails oauth2UserDetails;

            switch (provider.toUpperCase()) {
                case "GOOGLE":
                    oauth2UserDetails = new OAuth2GoogleUser(oauth2User.getAttributes());
                    break;
                case "FACEBOOK":
                    oauth2UserDetails = new OAuth2FacebookUser(oauth2User.getAttributes());
                    break;
                case "GITHUB":
                    oauth2UserDetails = new OAuth2GithubUser(oauth2User.getAttributes());
                    break;
                default:
                    throw new RuntimeException("Provider không được hỗ trợ: " + provider);
            }

            return new OAuth2User() {
                @Override
                public Map<String, Object> getAttributes() {
                    return oauth2User.getAttributes();
                }

                @Override
                public Collection<? extends GrantedAuthority> getAuthorities() {
                    return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
                }

                @Override
                public String getName() {
                    String name = oauth2UserDetails.getId();
                    if (name == null || name.isEmpty()) {
                        name = oauth2UserDetails.getEmail(); // fallback
                    }
                    return name;
                }
            };
        } catch (Exception e) {
            log.error("Lỗi: {}", e.getMessage());
            throw new OAuth2AuthenticationException("Không thể xử lý thông tin người dùng OAuth2");
        }
    }
}