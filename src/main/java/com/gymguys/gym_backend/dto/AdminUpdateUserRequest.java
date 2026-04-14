package com.gymguys.gym_backend.dto;

public class AdminUpdateUserRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String newPassword;   // optional – leave null/blank to keep existing
    private Double weight;
    private Integer splitDays;
    private Boolean profileCompleted;
    private String profilePicUrl;

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public Integer getSplitDays() { return splitDays; }
    public void setSplitDays(Integer splitDays) { this.splitDays = splitDays; }

    public Boolean getProfileCompleted() { return profileCompleted; }
    public void setProfileCompleted(Boolean profileCompleted) { this.profileCompleted = profileCompleted; }

    public String getProfilePicUrl() { return profilePicUrl; }
    public void setProfilePicUrl(String profilePicUrl) { this.profilePicUrl = profilePicUrl; }
}
