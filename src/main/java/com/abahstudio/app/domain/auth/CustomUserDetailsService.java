package com.abahstudio.app.domain.auth;

import com.abahstudio.app.core.exception.ApiException;
import com.abahstudio.app.core.exception.ErrorCode;
import com.abahstudio.app.domain.role.entity.Role;
import com.abahstudio.app.domain.user.User;
import com.abahstudio.app.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmailWithRoles(usernameOrEmail)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        Set<GrantedAuthority> authorities = new HashSet<>();

        // ROLE
        user.getUserRoles().forEach(ur -> {
            Role role = ur.getRole();
            authorities.add(
                    new SimpleGrantedAuthority("ROLE_" + role.getCode())
            );

            // PERMISSION
            role.getRolePermissions().forEach(rp -> {
                authorities.add(
                        new SimpleGrantedAuthority(
                                rp.getPermission().getCode()
                        )
                );
            });
        });

        return new CustomUserPrincipal(
                user.getId(),
                user.getCompanyCode(),
                user.getUsername(),
                user.getPassword(),
                List.of() // roles / authorities
        );
    }
}
