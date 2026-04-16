package com.gymguys.gym_backend.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gymguys.gym_backend.dto.AuthRequest;
import com.gymguys.gym_backend.dto.AuthResponse;
import com.gymguys.gym_backend.dto.SignupRequest;
import com.gymguys.gym_backend.entity.User;
import com.gymguys.gym_backend.security.CustomUserDetailsService;
import com.gymguys.gym_backend.security.JwtService;

@Service
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    public AuthService(UserService userService,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       CustomUserDetailsService customUserDetailsService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
    }

    public AuthResponse signup(SignupRequest request) {
        User savedUser = userService.signup(request);

        var userDetails = customUserDetailsService.loadUserByUsername(savedUser.getEmail());
        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                token,
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getProfileCompleted(),
                savedUser.getSplitDays(),
                savedUser.getRole(),
                savedUser.getProfilePicUrl()
        );
    }

    public AuthResponse login(AuthRequest request) {
        User user = userService.getByEmail(request.getEmail());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        if (user.getSuspended()) {
            throw new RuntimeException("Your account has been suspended. Please contact admin.");
        }

        var userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                token,
                user.getFirstName(),
                user.getLastName(),
                user.getProfileCompleted(),
                user.getSplitDays(),
                user.getRole(),
                user.getProfilePicUrl()
        );
    }
}