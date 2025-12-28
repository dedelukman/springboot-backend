package com.abahstudio.app.domain.auth;

import com.abahstudio.app.core.security.JwtCookieUtil;
import com.abahstudio.app.core.security.JwtUtil;
import com.abahstudio.app.domain.company.Company;
import com.abahstudio.app.domain.company.CompanyService;
import com.abahstudio.app.domain.subscription.service.SubscriptionService;
import com.abahstudio.app.domain.user.User;
import com.abahstudio.app.domain.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final JwtCookieUtil cookieUtil;
    private final CompanyService companyService;
    private final SubscriptionService subscriptionService;

    // =================================
    //              LOGIN
    // =================================
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        try {
            // First try to find user by email
            User user = userService.getUserByUsername(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Then authenticate
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String accessToken = jwtUtil.generateAccessToken(user.getUsername());
            String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

            cookieUtil.setAccessToken(response, accessToken);
            cookieUtil.setRefreshToken(response, refreshToken);


            return ResponseEntity.ok(Map.of(
                    "id", user.getId(),
                    "name", user.getFullName(),
                    "email", user.getEmail(),
                    "role", user.getRole().name(),
                    "username", user.getUsername()
            ));
        } catch (Exception e) {
            log.error("Login error: ", e);
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
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request,
                                      HttpServletResponse response) {

        // Cek email
        if (userService.existsByUsername(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email is already taken");
        }
        Company entity = new Company();
        Company company = companyService.create(entity);

        subscriptionService.subscribe(company.getCode());

        // Buat user baru
        User user = new User();
        user.setFullName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // service harus encode!
        user.setCompany(company);
        user.setCompanyCode(company.getCode());
        user.setRole(Role.USER);

        // Simpan user
        User savedUser = userService.createUser(user);

        // Generate JWT berdasarkan savedUser
        String accessToken = jwtUtil.generateAccessToken(savedUser.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(savedUser.getUsername());

        // Set cookie
        cookieUtil.setAccessToken(response, accessToken);
        cookieUtil.setRefreshToken(response, refreshToken);

        // React expects this:
        return ResponseEntity.ok(Map.of(
                "id", savedUser.getId(),
                "name", savedUser.getFullName(),
                "email", savedUser.getEmail(),
                "role", savedUser.getRole().name(),
                "username", savedUser.getUsername()
        ));
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
