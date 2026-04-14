package com.gymguys.gym_backend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.gymguys.gym_backend.dto.AdminUpdateUserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gymguys.gym_backend.dto.WorkoutRequestDTO;
import com.gymguys.gym_backend.entity.DailyWorkout;
import com.gymguys.gym_backend.entity.User;
import com.gymguys.gym_backend.entity.WorkoutConfig;
import com.gymguys.gym_backend.entity.WorkoutRequest;
import com.gymguys.gym_backend.repository.DailyWorkoutRepository;
import com.gymguys.gym_backend.repository.UserRepository;
import com.gymguys.gym_backend.repository.WorkoutConfigRepository;
import com.gymguys.gym_backend.repository.WorkoutRequestRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class AdminService {

    private static final Logger log = LoggerFactory.getLogger(AdminService.class);

    private final UserRepository userRepository;
    private final WorkoutConfigRepository configRepository;
    private final WorkoutRequestRepository requestRepository;
    private final DailyWorkoutRepository workoutRepository;
    private final WorkoutGeneratorService generatorService;
    private final PasswordEncoder passwordEncoder;

    public AdminService(UserRepository userRepository,
                        WorkoutConfigRepository configRepository,
                        WorkoutRequestRepository requestRepository,
                        DailyWorkoutRepository workoutRepository,
                        WorkoutGeneratorService generatorService,
                        PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.configRepository = configRepository;
        this.requestRepository = requestRepository;
        this.workoutRepository = workoutRepository;
        this.generatorService = generatorService;
        this.passwordEncoder = passwordEncoder;
    }

    // ✅ Get all users
    public List<User> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll();
    }

    // ✅ Update user split
    public User updateUserSplit(Long userId, int days) {
        log.info("Updating splitDays={} for userId={}", days, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setSplitDays(days);
        return userRepository.save(user);
    }

    // ✅ Update role
    public User updateUserRole(Long userId, String role) {
        log.info("Updating role={} for userId={}", role, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRole(role.toUpperCase());
        return userRepository.save(user);
    }

    // ✅ Get configs
    public List<WorkoutConfig> getAllConfigs() {
        log.info("Fetching all workout configs");
        return configRepository.findAll();
    }

    // ✅ Add config
    public WorkoutConfig addConfig(WorkoutConfig config) {
        log.info("Adding workout config: {}", config.getMuscleGroup());
        return configRepository.save(config);
    }

    // ✅ Delete config
    public void deleteConfig(Long id) {
        log.info("Deleting workout config id={}", id);

        if (!configRepository.existsById(id)) {
            throw new RuntimeException("Workout config not found");
        }

        configRepository.deleteById(id);
    }

    // ✅ Get all pending workout requests (with user info)
    public List<WorkoutRequestDTO> getPendingWorkoutRequests() {
        log.info("Fetching pending workout requests");

        return requestRepository
                .findByStatusOrderByRequestedAtAsc(WorkoutRequest.RequestStatus.PENDING)
                .stream()
                .map(req -> {
                    String name = "User #" + req.getUserId();
                    String email = "";
                    var userOpt = userRepository.findById(req.getUserId());
                    if (userOpt.isPresent()) {
                        User u = userOpt.get();
                        String full = ((u.getFirstName() != null ? u.getFirstName() : "")
                                + " " + (u.getLastName() != null ? u.getLastName() : "")).trim();
                        name = full.isEmpty() ? name : full;
                        email = u.getEmail() != null ? u.getEmail() : "";
                    }
                    return new WorkoutRequestDTO(
                            req.getId(),
                            req.getUserId(),
                            name,
                            email,
                            req.getRequestedAt()
                    );
                })
                .collect(Collectors.toList());
    }

    // ✅ Admin generates Day-1 workout for a user immediately
    public DailyWorkout generateWorkoutForUser(Long userId) {
        log.info("Admin generating Day-1 workout for userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        if (user.getSplitDays() == null || user.getSplitDays() <= 0) {
            throw new RuntimeException("User has no valid splitDays configured. Set splitDays first.");
        }

        LocalDate today = LocalDate.now();

        // Idempotent: if workout already exists today, return it
        var existing = workoutRepository.findByUserIdAndWorkoutDate(userId, today);
        if (existing.isPresent()) {
            log.info("Workout already exists for userId={} on {}", userId, today);
            markRequestGenerated(userId);
            return existing.get();
        }

        // Generate muscles (retry up to 5 times for variety)
        List<String> muscles = Collections.emptyList();
        for (int i = 0; i < 5; i++) {
            muscles = generatorService.generateTodayWorkout(user.getSplitDays());
            if (!muscles.isEmpty()) break;
        }

        if (muscles.isEmpty()) {
            throw new RuntimeException("No workout configs found. Add configs first.");
        }

        DailyWorkout workout = new DailyWorkout(
                userId,
                1, // Always Day 1 for manually triggered first workout
                String.join(",", muscles),
                today
        );

        workoutRepository.save(workout);
        log.info("💪 Admin-generated workout for userId={}: Day=1 {}", userId, muscles);

        markRequestGenerated(userId);

        return workout;
    }

    private void markRequestGenerated(Long userId) {
        requestRepository
                .findFirstByUserIdAndStatus(userId, WorkoutRequest.RequestStatus.PENDING)
                .ifPresent(req -> {
                    req.setStatus(WorkoutRequest.RequestStatus.GENERATED);
                    req.setGeneratedAt(LocalDateTime.now());
                    requestRepository.save(req);
                });
    }

    // ✅ Delete user (and their workout data)
    public void deleteUser(Long userId) {
        log.info("Deleting userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            throw new RuntimeException("Cannot delete an admin account");
        }

        // Delete associated data
        workoutRepository.findByUserId(userId).forEach(w -> workoutRepository.deleteById(w.getId()));
        requestRepository.findByUserId(userId).forEach(r -> requestRepository.deleteById(r.getId()));

        userRepository.deleteById(userId);
        log.info("userId={} deleted successfully", userId);
    }

    // ✅ Update all user details
    public User updateUserDetails(Long userId, AdminUpdateUserRequest req) {
        log.info("Admin updating details for userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (req.getFirstName() != null) user.setFirstName(req.getFirstName());
        if (req.getLastName() != null) user.setLastName(req.getLastName());
        if (req.getEmail() != null && !req.getEmail().isBlank()) {
            // Ensure no duplicate email
            if (!req.getEmail().equalsIgnoreCase(user.getEmail())
                    && userRepository.existsByEmail(req.getEmail())) {
                throw new RuntimeException("Email already in use by another account");
            }
            user.setEmail(req.getEmail());
        }
        if (req.getNewPassword() != null && !req.getNewPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        }
        if (req.getWeight() != null) user.setWeight(req.getWeight());
        if (req.getSplitDays() != null) user.setSplitDays(req.getSplitDays());
        if (req.getProfileCompleted() != null) user.setProfileCompleted(req.getProfileCompleted());
        if (req.getProfilePicUrl() != null) user.setProfilePicUrl(req.getProfilePicUrl());

        return userRepository.save(user);
    }

    // ✅ Suspend or unsuspend a user
    public User setSuspended(Long userId, boolean suspended) {
        log.info("Setting suspended={} for userId={}", suspended, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            throw new RuntimeException("Cannot suspend an admin account");
        }

        user.setSuspended(suspended);
        return userRepository.save(user);
    }
}