package com.example.vibely_backend.service;

import com.example.vibely_backend.entity.Admin;
import com.example.vibely_backend.entity.AdminPrincipal;
import com.example.vibely_backend.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Qualifier;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Qualifier("adminDetailsService")
public class MyAdminDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(MyAdminDetailsService.class);

    @Autowired
    private AdminRepository adminRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Loading admin by email: {}", email);

        Admin admin = adminRepo.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Admin not found with email: {}", email);
                    return new UsernameNotFoundException("Không tìm thấy admin với email: " + email);
                });

        log.info("Admin found: {}", admin.getEmail());
        return new AdminPrincipal(admin);
    }
}
