package com.abahstudio.app.core.security;

import com.abahstudio.app.domain.auth.CustomUserPrincipal;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SecurityUtil {

    public CustomUserPrincipal getPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new AuthenticationCredentialsNotFoundException("Unauthenticated");
        }

        if (!(auth.getPrincipal() instanceof CustomUserPrincipal principal)) {
            throw new IllegalStateException("Principal is not CustomUserPrincipal");
        }

        return principal;
    }

    public UUID getUserId() {
        return getPrincipal().getUserId();
    }

    public String getCompanyCode() {
        return getPrincipal().getCompanyCode();
    }

    public String getUsername() {
        return getPrincipal().getUsername();
    }
}

