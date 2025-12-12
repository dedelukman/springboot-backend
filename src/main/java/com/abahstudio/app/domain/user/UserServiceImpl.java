package com.abahstudio.app.domain.user;

import com.abahstudio.app.core.exception.ApiException;
import com.abahstudio.app.core.exception.ErrorCode;
import com.abahstudio.app.domain.auth.AuthService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByUsername(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
    }

    @Override
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User updateUser(UUID id, User userDetails, HttpServletResponse response) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        boolean usernameChanged = !user.getUsername().equals(userDetails.getUsername());
        boolean emailChanged = !user.getEmail().equals(userDetails.getEmail());

        if (usernameChanged && existsByUsername(userDetails.getUsername())) {
            throw new ApiException(ErrorCode.USERNAME_ALREADY_TAKEN);
        }
        if (emailChanged && existsByUsername(userDetails.getEmail())) {
            throw new ApiException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        user.setUsername(userDetails.getUsername());
        user.setFullName(userDetails.getFullName());
        user.setEmail(userDetails.getEmail());

        if (userDetails.getPassword() != null && !userDetails.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }

        if (usernameChanged) {
            authService.reAuthenticate(user, response);
        }

        return userRepository.save(user);
    }

    @Override
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        userRepository.delete(user);
    }

    @Override
    public boolean existsByUsername(String usernameOrEmail) {
        return userRepository.existsByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
    }
}