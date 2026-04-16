package com.gymguys.gym_backend.controller;

import com.gymguys.gym_backend.dto.ApiResponse;
import com.gymguys.gym_backend.dto.ProfileUpdateRequest;
import com.gymguys.gym_backend.entity.User;
import com.gymguys.gym_backend.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Update profile (only self)
     */
    @PutMapping("/{userId}")
    public ApiResponse<User> updateProfile(@PathVariable Long userId,
                                           @RequestBody ProfileUpdateRequest request,
                                           Authentication authentication) {

        User loggedInUser = userService.getByEmail(authentication.getName());

        if (!loggedInUser.getId().equals(userId)) {
            throw new RuntimeException("You can update only your own profile");
        }

        User updatedUser = userService.updateProfile(userId, request);

        return new ApiResponse<>(true, "Profile updated successfully", updatedUser);
    }

    @PostMapping("/{userId}/profile-pic")
    public ApiResponse<User> uploadProfilePic(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        User loggedInUser = userService.getByEmail(authentication.getName());

        if (!loggedInUser.getId().equals(userId)) {
            throw new RuntimeException("You can update only your own profile");
        }

        User updatedUser = userService.uploadProfilePic(userId, file);

        return new ApiResponse<>(true, "Profile picture updated", updatedUser);
    }

    @DeleteMapping("/{userId}/profile-pic")
    public ApiResponse<User> deleteProfilePic(
            @PathVariable Long userId,
            Authentication authentication) {

        User loggedInUser = userService.getByEmail(authentication.getName());

        if (!loggedInUser.getId().equals(userId)) {
            throw new RuntimeException("You can update only your own profile");
        }

        User updatedUser = userService.deleteProfilePic(userId);

        return new ApiResponse<>(true, "Profile picture removed", updatedUser);
    }

    /**
     * Get logged-in user profile
     */
    @GetMapping("/me")
    public ApiResponse<User> getMyProfile(Authentication authentication) {

        User user = userService.getByEmail(authentication.getName());

        return new ApiResponse<>(true, "Profile fetched successfully", user);
    }
}