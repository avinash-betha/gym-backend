package com.gymguys.gym_backend.dto;

public class AuthResponse {

    private Long userId;
    private String email;
    private String token;
    private String firstName;
    private String lastName;
    private Boolean profileCompleted;
    private Integer splitDays;
    private String role;
    private String profilePicUrl;

    public AuthResponse() {
    }

    public AuthResponse(Long userId,
                        String email,
                        String token,
                        String firstName,
                        String lastName,
                        Boolean profileCompleted,
                        Integer splitDays,
                        String role,
                        String profilePicUrl) {
        this.userId = userId;
        this.email = email;
        this.token = token;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profileCompleted = profileCompleted;
        this.splitDays = splitDays;
        this.role = role;
        this.profilePicUrl = profilePicUrl;
    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Boolean getProfileCompleted() {
        return profileCompleted;
    }

    public Integer getSplitDays() {
        return splitDays;
    }

    public String getRole() {
        return role;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setProfileCompleted(Boolean profileCompleted) {
        this.profileCompleted = profileCompleted;
    }

    public void setSplitDays(Integer splitDays) {
        this.splitDays = splitDays;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }
}