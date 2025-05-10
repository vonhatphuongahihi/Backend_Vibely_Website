package com.example.vibely_backend.config;

import com.example.vibely_backend.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);

    @Autowired
    private JWTService jwtService;

    @Autowired
    @Qualifier("myUserDetailsService")
    private UserDetailsService userDetailsService;

    @Autowired
    @Qualifier("myAdminDetailsService")
    private UserDetailsService adminDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String authHeader = request.getHeader("Authorization");
            log.debug("Received Authorization header: {}", authHeader);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.debug("No valid Authorization header found");
                filterChain.doFilter(request, response);
                return;
            }

            String token = authHeader.substring(7);
            log.debug("Extracted token: {}", token);

            String email = jwtService.extractEmail(token);
            log.debug("Extracted email from token: {}", email);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = null;
                try {
                    // Thử load user trước
                    userDetails = userDetailsService.loadUserByUsername(email);
                } catch (UsernameNotFoundException e) {
                    try {
                        // Nếu không tìm thấy user, thử load admin
                        userDetails = adminDetailsService.loadUserByUsername(email);
                    } catch (UsernameNotFoundException ex) {
                        log.warn("Không tìm thấy user hoặc admin với email: {}", email);
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        return;
                    }
                }

                if (jwtService.validateToken(token, userDetails, email)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            token,
                            userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("Authentication successful for: {}", email);
                } else {
                    log.warn("Token validation failed for: {}", email);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Error processing JWT token: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/auth/") || path.startsWith("/admin/auth/");
    }
}
