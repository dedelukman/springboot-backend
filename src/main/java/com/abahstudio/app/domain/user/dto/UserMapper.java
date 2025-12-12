package com.abahstudio.app.domain.user.dto;

import com.abahstudio.app.domain.auth.Role;
import com.abahstudio.app.domain.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    // CREATE: Request -> Entity
    public User toEntity(UserRequest request) {
        if (request == null) return null;

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());

        if (request.getRole() != null) {
            user.setRole(Role.valueOf(request.getRole()));
        }

        if (request.getEnabled() != null) {
            user.setEnabled(request.getEnabled());
        }

        if (request.getLocked() != null) {
            user.setLocked(request.getLocked());
        }

        return user;
    }

    // UPDATE: hanya isi field yang tidak null
    public void updateEntity(UserRequest request, User user) {
        if (request == null || user == null) return;

        if (request.getUsername() != null) user.setUsername(request.getUsername());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getPassword() != null) user.setPassword(request.getPassword());
        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());

        if (request.getRole() != null) {
            user.setRole(Role.valueOf(request.getRole()));
        }
        if (request.getEnabled() != null) {
            user.setEnabled(request.getEnabled());
        }
        if (request.getLocked() != null) {
            user.setLocked(request.getLocked());
        }
    }

    // Entity -> Response
    public UserResponse toResponse(User user) {
        if (user == null) return null;

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole() != null ? user.getRole().name() : null);
        response.setEnabled(user.isEnabled());
        response.setLocked(user.isLocked());

        // Dari CompanyScopedEntity
//        response.setCompanyCode(user.getCompanyCode());

        return response;
    }
}