package com.abahstudio.app.domain.user;

import com.abahstudio.app.domain.user.dto.UserRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    List<User> getAllUsers();

    Optional<User> getUserById(UUID id);

    Optional<User> getUserByUsername(String usernameOrEmail);

    User createUser(User user);

    User updateUser(UUID id, UserRequest request, HttpServletResponse response);

    void deleteUser(UUID id);

    boolean existsByUsername(String usernameOrEmail);
}