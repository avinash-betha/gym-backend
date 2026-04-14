package com.gymguys.gym_backend.dto;

public class ProfileUpdateRequest {

    private Double weight;
    private String profilePicUrl;
    private Integer splitDays;

    // 🔥 NEW FIELDS
    private String firstName;
    private String lastName;
    private String email;

    public ProfileUpdateRequest() {
    }

    // ✅ GETTERS & SETTERS

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public Integer getSplitDays() {
        return splitDays;
    }

    public void setSplitDays(Integer splitDays) {
        this.splitDays = splitDays;
    }

    // 🔥 NEW METHODS

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}