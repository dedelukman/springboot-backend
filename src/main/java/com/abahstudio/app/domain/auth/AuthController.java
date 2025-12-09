package com.abahstudio.app.domain.auth;

import com.abahstudio.app.core.security.JwtCookieUtil;
import com.abahstudio.app.core.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final JwtCookieUtil cookieUtil;

    // =================================
    //              LOGIN
    // =================================
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Get the principal based on your implementation
            Object principal = authentication.getPrincipal();
            User user;

            if (principal instanceof User) {
                user = (User) principal;
            } else {
                // If principal is Spring Security's User, you might need to load your custom User
                String email = ((org.springframework.security.core.userdetails.User) principal).getUsername();
                user = userService.getUserByUsername(email)
                        .orElseThrow(() -> new RuntimeException("User not found"));
            }


            String accessToken = jwtUtil.generateAccessToken(user.getEmail());
            String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

            cookieUtil.setAccessToken(response, accessToken);
            cookieUtil.setRefreshToken(response, refreshToken);

            return ResponseEntity.ok(Map.of(
                    "message", "Login success",
                    "username", user.getUsername()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid email or password");
        }

    }


    // =================================
    //              REFRESH
    // =================================
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String refreshToken = cookieUtil.getRefreshToken(request);
        if (refreshToken == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Refresh token missing"));
        }

        // Validasi
        if (!jwtUtil.isRefreshTokenValid(refreshToken)) {
            return ResponseEntity.status(401).body(Map.of("error", "Refresh token invalid"));
        }

        String username = jwtUtil.extractUsernameFromRefresh(refreshToken);

        // Generate access baru
        String newAccess = jwtUtil.generateAccessToken(username);
        cookieUtil.setAccessToken(response, newAccess);

        return ResponseEntity.ok(Map.of(
                "message", "Access token refreshed",
                "username", username
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request, HttpServletResponse response) {
        try {
            if (userService.existsByUsername(request.getEmail())) {
                return ResponseEntity.badRequest().body("Email is already taken");
            }

            User user = new User();
            user.setFullName(request.getName());
            user.setEmail(request.getEmail());
            user.setPassword(request.getPassword());
            user.setRole(Role.USER);

            User savedUser = userService.createUser(user);
            if(savedUser == null){
                return ResponseEntity.badRequest().body("User registration failed");
            }

            // Create authentication for new user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            String accessToken = jwtUtil.generateAccessToken(user.getEmail());
            String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

            // Generate JWT token for new user
            cookieUtil.setAccessToken(response, accessToken);
            cookieUtil.setRefreshToken(response, refreshToken);

            return ResponseEntity.ok(Map.of(
                    "message", "Login success",
                    "username", user.getUsername()
            ));
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body("Invalid email or password");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Registration failed");
        }

    }

    // =================================
    //              LOGOUT
    // =================================
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        cookieUtil.clearTokens(response); // hapus cookie

        return ResponseEntity.ok(Map.of("message", "Logout success"));
    }
}
