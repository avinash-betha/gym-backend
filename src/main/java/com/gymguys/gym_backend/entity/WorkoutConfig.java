package com.gymguys.gym_backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "workout_config")
public class WorkoutConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Example: "Chest,Triceps"
    private String muscleGroup;

    // Optional difficulty or type
    private String type; // PUSH / PULL / LEG

    public WorkoutConfig() {}

    public WorkoutConfig(String muscleGroup, String type) {
        this.muscleGroup = muscleGroup;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public String getMuscleGroup() {
        return muscleGroup;
    }

    public void setMuscleGroup(String muscleGroup) {
        this.muscleGroup = muscleGroup;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}