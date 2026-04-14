package com.gymguys.gym_backend.dto;

import java.util.List;

public class WorkoutPlanDTO {

    private int day;
    private List<String> muscleGroups;

    public WorkoutPlanDTO() {}

    public WorkoutPlanDTO(int day, List<String> muscleGroups) {
        this.day = day;
        this.muscleGroups = muscleGroups;
    }

    public int getDay() {
        return day;
    }

    public List<String> getMuscleGroups() {
        return muscleGroups;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setMuscleGroups(List<String> muscleGroups) {
        this.muscleGroups = muscleGroups;
    }
}