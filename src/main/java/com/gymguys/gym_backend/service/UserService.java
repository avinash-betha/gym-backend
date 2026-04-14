package com.gymguys.gym_backend.service;

import com.gymguys.gym_backend.dto.ProfileUpdateRequest;
import com.gymguys.gym_backend.dto.SignupRequest;
import com.gymguys.gym_backend.entity.User;
import com.gymguys.gym_backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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

        user.setWeight(request.getWeight());
        user.setProfilePicUrl(request.getProfilePicUrl());
        user.setSplitDays(request.getSplitDays());

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

        User updated = userRepository.save(user);
        log.info("Profile updated for userId={}", updated.getId());

        return updated;
    }
}