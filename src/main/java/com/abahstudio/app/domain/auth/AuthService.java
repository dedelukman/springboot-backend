package com.abahstudio.app.domain.auth;

import com.abahstudio.app.core.security.JwtCookieUtil;
import com.abahstudio.app.core.security.JwtUtil;
import com.abahstudio.app.domain.user.User;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final JwtCookieUtil cookieUtil;

    public void reAuthenticate(User user, HttpServletResponse response) {
        // Reload UserDetails terbaru
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        // Set authentication baru
        UsernamePasswordAuthenticationToken newAuth =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        SecurityContextHolder.getContext().setAuthentication(newAuth);

        String accessToken = jwtUtil.generateAccessToken(user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        cookieUtil.setAccessToken(response, accessToken);
        cookieUtil.setRefreshToken(response, refreshToken);
    }
}

