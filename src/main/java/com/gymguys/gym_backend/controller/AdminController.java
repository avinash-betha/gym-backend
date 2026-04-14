package com.gymguys.gym_backend.controller;

import java.util.List;
import java.util.Map;

import com.gymguys.gym_backend.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gymguys.gym_backend.entity.DailyWorkout;
import com.gymguys.gym_backend.entity.User;
import com.gymguys.gym_backend.entity.WorkoutConfig;
import com.gymguys.gym_backend.service.AdminService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // 1. GET /api/admin/users
    @GetMapping("/users")
    public ApiResponse<List<User>> getAllUsers() {
        log.info("Admin: fetching all users");
        return new ApiResponse<>(true, "Users fetched", adminService.getAllUsers());
    }

    // 2. PUT /api/admin/users/{id}/split
    @PutMapping("/users/{id}/split")
    public ApiResponse<User> updateSplit(@PathVariable Long id,
                                          @RequestBody UpdateSplitRequest request) {
        log.info("Admin: updating splitDays={} for userId={}", request.getSplitDays(), id);
        return new ApiResponse<>(true, "Split updated",
                adminService.updateUserSplit(id, request.getSplitDays()));
    }

    // 3. PUT /api/admin/users/{id}/role
    @PutMapping("/users/{id}/role")
    public ApiResponse<User> updateRole(@PathVariable Long id,
                                         @RequestBody UpdateRoleRequest request) {
        log.info("Admin: updating role={} for userId={}", request.getRole(), id);
        return new ApiResponse<>(true, "Role updated",
                adminService.updateUserRole(id, request.getRole()));
    }

    // 4. GET /api/admin/configs
    @GetMapping("/configs")
    public ApiResponse<List<WorkoutConfig>> getAllConfigs() {
        log.info("Admin: fetching all workout configs");
        return new ApiResponse<>(true, "Configs fetched", adminService.getAllConfigs());
    }

    // 5. POST /api/admin/configs
    @PostMapping("/configs")
    public ApiResponse<WorkoutConfig> addConfig(@RequestBody WorkoutConfig config) {
        log.info("Admin: adding workout config for muscleGroup={}", config.getMuscleGroup());
        return new ApiResponse<>(true, "Config added", adminService.addConfig(config));
    }

    // 6. DELETE /api/admin/configs/{id}
    @DeleteMapping("/configs/{id}")
    public ApiResponse<Void> deleteConfig(@PathVariable Long id) {
        log.info("Admin: deleting workout config id={}", id);
        adminService.deleteConfig(id);
        return new ApiResponse<>(true, "Config deleted", null);
    }

    // 7. GET /api/admin/workout-requests  — pending user requests
    @GetMapping("/workout-requests")
    public ApiResponse<List<WorkoutRequestDTO>> getWorkoutRequests() {
        log.info("Admin: fetching pending workout requests");
        return new ApiResponse<>(true, "Requests fetched", adminService.getPendingWorkoutRequests());
    }

    // 8. POST /api/admin/workout-requests/{userId}/generate  — generate Day-1 workout now
    @PostMapping("/workout-requests/{userId}/generate")
    public ApiResponse<DailyWorkout> generateWorkoutNow(@PathVariable Long userId) {
        log.info("Admin: generating workout for userId={}", userId);
        DailyWorkout workout = adminService.generateWorkoutForUser(userId);
        return new ApiResponse<>(true, "Workout generated successfully", workout);
    }

    // 9. DELETE /api/admin/users/{id}
    @DeleteMapping("/users/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        log.info("Admin: deleting userId={}", id);
        adminService.deleteUser(id);
        return new ApiResponse<>(true, "User deleted", null);
    }

    // 10. PUT /api/admin/users/{id}/details
    @PutMapping("/users/{id}/details")
    public ApiResponse<User> updateUserDetails(@PathVariable Long id,
                                               @RequestBody AdminUpdateUserRequest request) {
        log.info("Admin: updating details for userId={}", id);
        return new ApiResponse<>(true, "User updated",
                adminService.updateUserDetails(id, request));
    }

    // 11. PUT /api/admin/users/{id}/suspend
    @PutMapping("/users/{id}/suspend")
    public ApiResponse<User> suspendUser(@PathVariable Long id,
                                         @RequestBody Map<String, Boolean> body) {
        boolean suspended = Boolean.TRUE.equals(body.get("suspended"));
        log.info("Admin: setting suspended={} for userId={}", suspended, id);
        return new ApiResponse<>(true, "User suspension updated",
                adminService.setSuspended(id, suspended));
    }
}
