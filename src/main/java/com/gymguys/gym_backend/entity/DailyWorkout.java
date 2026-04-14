package com.gymguys.gym_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "daily_workout")
public class DailyWorkout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private int day;

    private String muscleGroups; // store as comma-separated

    private LocalDate workoutDate;

    public DailyWorkout() {}

    public DailyWorkout(Long userId, int day, String muscleGroups, LocalDate workoutDate) {
        this.userId = userId;
        this.day = day;
        this.muscleGroups = muscleGroups;
        this.workoutDate = workoutDate;
    }

    public Long getId() { return id; }

    public Long getUserId() { return userId; }

    public int getDay() { return day; }

    public String getMuscleGroups() { return muscleGroups; }

    public LocalDate getWorkoutDate() { return workoutDate; }

    public void setUserId(Long userId) { this.userId = userId; }

    public void setDay(int day) { this.day = day; }

    public void setMuscleGroups(String muscleGroups) { this.muscleGroups = muscleGroups; }

    public void setWorkoutDate(LocalDate workoutDate) { this.workoutDate = workoutDate; }
}