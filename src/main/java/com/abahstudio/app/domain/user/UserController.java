package com.abahstudio.app.domain.user;

import com.abahstudio.app.core.exception.ApiException;
import com.abahstudio.app.domain.user.dto.UserMapper;
import com.abahstudio.app.domain.user.dto.UserRequest;
import com.abahstudio.app.domain.user.dto.UserResponse;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper mapper;

    // GET ALL USERS
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> responses = userService.getAllUsers()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    // GET USER BY ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        return userService.getUserById(id)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // REGISTER USER
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRequest request) {

        if (userService.existsByUsername(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email is already taken");
        }

        User entity = mapper.toEntity(request);
        User saved = userService.createUser(entity);

        return new ResponseEntity<>(mapper.toResponse(saved), HttpStatus.CREATED);
    }

    // UPDATE USER
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable UUID id,
            @RequestBody UserRequest request,
            HttpServletResponse response) {

        try {
            User existing = userService.getUserById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // apply DTO → entity updates
            mapper.updateEntity(request, existing);

            // let service handle password encode + re-auth
            User updated = userService.updateUser(id, existing, response);

            return ResponseEntity.ok(mapper.toResponse(updated));

        } catch (ApiException e) {
            return new ResponseEntity<>(e.getErrorCode().getCode(), e.getErrorCode().getStatus());
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE USER
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // CURRENT LOGGED USER
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication auth) {

        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = auth.getName();

        return userService.getUserByUsername(username)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}