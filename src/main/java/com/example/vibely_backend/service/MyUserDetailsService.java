package com.example.vibely_backend.service;

import com.example.vibely_backend.entity.UserPrincipal;
import com.example.vibely_backend.entity.User;
import com.example.vibely_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Qualifier("myUserDetailsService")
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(usernameOrEmail)
                .orElseGet(() -> {
                    return userRepo.findByUsername(usernameOrEmail)
                            .orElse(null);
                });

        if (user == null) {
            throw new UsernameNotFoundException("Không tìm thấy username hoặc email: " + usernameOrEmail);
        }

        return new UserPrincipal(user);
    }
}