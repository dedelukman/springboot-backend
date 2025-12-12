package com.abahstudio.app.domain.user;

import com.abahstudio.app.domain.user.User;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> getAllUsers();

    Optional<User> getUserById(Long id);

    Optional<User> getUserByUsername(String usernameOrEmail);

    User createUser(User user);

    User updateUser(Long id, User userDetails, HttpServletResponse response);

    void deleteUser(Long id);

    boolean existsByUsername(String usernameOrEmail);
}
