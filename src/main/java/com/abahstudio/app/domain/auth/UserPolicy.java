package com.abahstudio.app.domain.auth;

import com.abahstudio.app.domain.user.User;
import com.abahstudio.app.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("userPolicy")
@RequiredArgsConstructor
public class UserPolicy {

    private final UserRepository userRepository;

    public boolean canEditUser(UUID targetUserId) {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        CustomUserDetails principal =
                (CustomUserDetails) auth.getPrincipal();

        User currentUser = principal.getUser();

        // 1️⃣ SUPER ADMIN BOLEH SEMUA
        if (hasRole(principal, "SUPER_ADMIN")) {
            return true;
        }

        // 2️⃣ ADMIN BOLEH DALAM COMPANY
        if (hasRole(principal, "ADMIN")) {
            return userRepository.existsByIdAndCompanyCode(
                    targetUserId,
                    currentUser.getCompanyCode()
            );
        }

        // 3️⃣ USER BIASA HANYA DIRI SENDIRI
        return currentUser.getId().equals(targetUserId);
    }

    private boolean hasRole(CustomUserDetails user, String role) {
        return user.getAuthorities().stream()
                .anyMatch(a ->
                        a.getAuthority().equals("ROLE_" + role));
    }
}

