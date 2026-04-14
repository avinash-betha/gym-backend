package com.gymguys.gym_backend.controller;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gymguys.gym_backend.dto.ApiResponse;
import com.gymguys.gym_backend.entity.DailyWorkout;
import com.gymguys.gym_backend.entity.User;
import com.gymguys.gym_backend.entity.WorkoutRequest;
import com.gymguys.gym_backend.repository.DailyWorkoutRepository;
import com.gymguys.gym_backend.repository.WorkoutRequestRepository;
import com.gymguys.gym_backend.service.UserService;
import com.gymguys.gym_backend.service.WorkoutGeneratorService;

@RestController
@RequestMapping("/api/workout")
public class WorkoutController {

    private static final Logger log = LoggerFactory.getLogger(WorkoutController.class);

    private final DailyWorkoutRepository workoutRepository;
    private final WorkoutGeneratorService generatorService;
    private final UserService userService;
    private final WorkoutRequestRepository requestRepository;

    public WorkoutController(DailyWorkoutRepository workoutRepository,
                             WorkoutGeneratorService generatorService,
                             UserService userService,
                             WorkoutRequestRepository requestRepository) {
        this.workoutRepository = workoutRepository;
        this.generatorService = generatorService;
        this.userService = userService;
        this.requestRepository = requestRepository;
    }

    /**
     * ✅ Get today's workout (SECURE)
     */
    @GetMapping("/today")
    public ApiResponse<DailyWorkout> getTodayWorkout(Authentication authentication) {

        String email = authentication.getName();
        User user = userService.getByEmail(email);

        log.info("📥 Fetching today's workout for userId={}", user.getId());

        DailyWorkout workout = workoutRepository
                .findByUserIdAndWorkoutDate(user.getId(), LocalDate.now())
                .orElse(null);

        if (workout == null) {
            log.info("⚠️ No workout found for userId={} on {}", user.getId(), LocalDate.now());
            return new ApiResponse<>(true, "No workout scheduled for today", null);
        }

        log.info("✅ Workout found for userId={}", user.getId());

        return new ApiResponse<>(true, "Workout fetched successfully", workout);
    }

    /**
     * ✅ Get workout history (SECURE)
     */
    @GetMapping("/history")
    public ApiResponse<List<DailyWorkout>> getWorkoutHistory(Authentication authentication) {

        String email = authentication.getName();
        User user = userService.getByEmail(email);

        log.info("📥 Fetching workout history for userId={}", user.getId());

        List<DailyWorkout> history = workoutRepository.findByUserId(user.getId());

        log.info("✅ Found {} records for userId={}", history.size(), user.getId());

        return new ApiResponse<>(true, "Workout history fetched successfully", history);
    }

    /**
     * 🧪 Manual generate (PUBLIC for testing)
     */
    @GetMapping("/generate/{splitDays}")
    public ApiResponse<List<String>> generateWorkout(@PathVariable int splitDays) {

        log.info("🧪 Manual workout generation requested for splitDays={}", splitDays);

        List<String> result = generatorService.generateTodayWorkout(splitDays);

        log.info("✅ Generated workout: {}", result);

        return new ApiResponse<>(true, "Workout generated successfully", result);
    }

    /**
     * ✅ User requests a workout for today (sends to admin queue)
     */
    @PostMapping("/request")
    public ApiResponse<Void> requestWorkout(Authentication authentication) {

        String email = authentication.getName();
        User user = userService.getByEmail(email);
        Long userId = user.getId();

        // Already has a workout today
        boolean hasWorkout = workoutRepository
                .findByUserIdAndWorkoutDate(userId, LocalDate.now())
                .isPresent();

        if (hasWorkout) {
            return new ApiResponse<>(false, "You already have a workout for today", null);
        }

        // Already has a pending request
        boolean alreadyRequested = requestRepository
                .existsByUserIdAndStatus(userId, WorkoutRequest.RequestStatus.PENDING);

        if (alreadyRequested) {
            return new ApiResponse<>(false, "Request already sent. Waiting for admin approval", null);
        }

        requestRepository.save(new WorkoutRequest(userId));
        log.info("📬 Workout request submitted for userId={}", userId);

        return new ApiResponse<>(true, "Request sent! Admin will generate your workout soon", null);
    }
}