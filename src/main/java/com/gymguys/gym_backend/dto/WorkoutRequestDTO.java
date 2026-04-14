package com.gymguys.gym_backend.dto;

import java.time.LocalDateTime;

public class WorkoutRequestDTO {

    private Long requestId;
    private Long userId;
    private String userName;
    private String userEmail;
    private LocalDateTime requestedAt;

    public WorkoutRequestDTO() {}

    public WorkoutRequestDTO(Long requestId, Long userId, String userName,
                              String userEmail, LocalDateTime requestedAt) {
        this.requestId = requestId;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.requestedAt = requestedAt;
    }

    public Long getRequestId() { return requestId; }
    public Long getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getUserEmail() { return userEmail; }
    public LocalDateTime getRequestedAt() { return requestedAt; }

    public void setRequestId(Long requestId) { this.requestId = requestId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }
}
