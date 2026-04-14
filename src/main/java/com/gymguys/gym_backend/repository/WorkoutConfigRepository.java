package com.gymguys.gym_backend.repository;

import com.gymguys.gym_backend.entity.WorkoutConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutConfigRepository extends JpaRepository<WorkoutConfig, Long> {
}