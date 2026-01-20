package com.abahstudio.app.domain.user;

import com.abahstudio.app.core.exception.ApiException;
import com.abahstudio.app.core.exception.ErrorCode;
import com.abahstudio.app.domain.file.FileEntity;
import com.abahstudio.app.domain.file.FileService;
import com.abahstudio.app.domain.user.dto.UserMapper;
import com.abahstudio.app.domain.user.dto.UserRequest;
import com.abahstudio.app.domain.user.dto.UserResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileService fileService;
    private final UserMapper mapper;

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
    public User updateUser(UUID id, UserRequest request, HttpServletResponse response) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        boolean emailChanged = !user.getEmail().equals(request.getEmail());

        if (emailChanged && existsByUsername(request.getEmail())) {
            throw new ApiException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
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

    @Transactional(readOnly = true)
    @Override
    public Optional<UserResponse> getUserWithAvatar(String usernameOrEmail) {

        return userRepository.findByUsernameOrEmailWithRoles(usernameOrEmail)
                .map(user -> {

                    UserResponse res = mapper.toResponse(user);

                    // ambil avatar (primary image)
                    fileService.findByOwner("USER", user.getId().toString())
                            .stream()
                            .findFirst()
                            .ifPresent(file -> {
                                res.setAvatarUrl(
                                        "files/view/" + file.getStorageKey()
                                );
                            });

                    return res;
                });
    }

    @Override
    public FileEntity upload(MultipartFile file, String ownerType, String ownerId) {

        Optional<FileEntity> oldAvatar =
                fileService.findPrimaryByOwner(ownerType, ownerId);

        FileEntity newAvatar = fileService.upload(file, ownerType, ownerId);

        newAvatar.setIsPrimary(true);
        FileEntity savedAvatar = fileService.save(newAvatar);

        oldAvatar.ifPresent(fileService::delete);

        return savedAvatar;

    }

    @Override
    public void deleteAvatar(String ownerType, String ownerId) {
        FileEntity avatar = fileService.findPrimaryByOwner(ownerType, ownerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Avatar not found"
                ));

        fileService.delete(avatar);
    }


}