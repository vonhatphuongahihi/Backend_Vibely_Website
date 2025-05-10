package com.example.vibely_backend.service;

import com.example.vibely_backend.entity.User;
import com.example.vibely_backend.entity.Provider;
import com.example.vibely_backend.dto.request.UserProfileUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class OAuth2Service {

    @Autowired
    private UserService userService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public User processOAuth2User(OAuth2User oauth2User) {
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String picture = oauth2User.getAttribute("picture");

        Optional<User> userOpt = userService.findByEmail(email);
        User user;

        if (userOpt.isEmpty()) {
            user = new User();
            user.setEmail(email);
            user.setUsername(name);
            user.setProfilePicture(picture);
            user.setProvider(Provider.GOOGLE);
            user.setPassword(encoder.encode("GOOGLE_" + email));
            user.setGender("Other");
            user.setDateOfBirth(LocalDate.now().minusYears(18));
            user = userService.register(user);
        } else {
            user = userOpt.get();
            // Cập nhật thông tin nếu cần
            if (user.getProvider() == null) {
                user.setProvider(Provider.GOOGLE);
                // Tạo UserProfileUpdateRequest từ thông tin user
                UserProfileUpdateRequest updateRequest = new UserProfileUpdateRequest();
                updateRequest.setUsername(user.getUsername());
                updateRequest.setEmail(user.getEmail());
                updateRequest.setGender(user.getGender());
                updateRequest.setDateOfBirth(user.getDateOfBirth());

                // Cập nhật profile với thông tin hiện tại
                user = userService.updateUserProfile(
                        user.getId(),
                        updateRequest,
                        user.getProfilePicture(),
                        user.getCoverPicture());
            }
        }

        return user;
    }
}