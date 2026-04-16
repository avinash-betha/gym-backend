package com.gymguys.gym_backend.service;

import com.gymguys.gym_backend.dto.ProfileUpdateRequest;
import com.gymguys.gym_backend.dto.SignupRequest;
import com.gymguys.gym_backend.entity.User;
import com.gymguys.gym_backend.repository.UserRepository;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MinioClient minioClient;
    @Value("${minio.url}")
    private String minioUrl;
    @Value("${minio.bucket}")
    private String bucket;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       MinioClient minioClient) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.minioClient = minioClient;
    }

    public User signup(SignupRequest request) {
        log.info("Signup requested for email={}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setWeight(request.getWeight());
        user.setProfilePicUrl(request.getProfilePicUrl());
        user.setProfileCompleted(false);
        user.setRole("USER");

        User saved = userRepository.save(user);
        log.info("User created successfully with id={}", saved.getId());

        return saved;
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User getById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User updateProfile(Long userId, ProfileUpdateRequest request) {
        log.info("Updating profile for userId={}", userId);

        User user = getById(userId);

        if (request.getWeight() != null) {
            user.setWeight(request.getWeight());
        }

        if (request.getSplitDays() != null) {
            user.setSplitDays(request.getSplitDays());
        }

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }

        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }

        user.setProfileCompleted(true);

        return userRepository.save(user);
    }

    public User uploadProfilePic(Long userId, MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        String contentType = file.getContentType();

        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Only image files are allowed");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("File size should be less than 5MB");
        }

        User user = getById(userId);

        try {
            String originalName = file.getOriginalFilename();

            if (originalName == null) {
                originalName = "image";
            }

            originalName = originalName
                    .replaceAll("\\s+", "_")
                    .replaceAll("[^a-zA-Z0-9._-]", "");

            if (originalName.isBlank()) {
                originalName = "image";
            }

            String fileName = "profile-pics/" +
                    userId + "-" + System.currentTimeMillis() + "-" + originalName;

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1L)
                            .contentType(contentType)
                            .build()
            );

            String fileUrl = minioUrl + "/" + bucket + "/" + fileName;

            user.setProfilePicUrl(fileUrl);

            return userRepository.save(user);

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload profile picture", e);
        }
    }

    public User deleteProfilePic(Long userId) {
        User user = getById(userId);
        user.setProfilePicUrl(null);
        return userRepository.save(user);
    }
}