package com.gymguys.gym_backend.repository;

import com.gymguys.gym_backend.entity.DailyWorkout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyWorkoutRepository extends JpaRepository<DailyWorkout, Long> {

    Optional<DailyWorkout> findTopByUserIdOrderByWorkoutDateDesc(Long userId);

    Optional<DailyWorkout> findByUserIdAndWorkoutDate(Long userId, LocalDate date);

    List<DailyWorkout> findByUserId(Long userId);
}