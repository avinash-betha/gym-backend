package com.gymguys.gym_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gymguys.gym_backend.entity.WorkoutRequest;

public interface WorkoutRequestRepository extends JpaRepository<WorkoutRequest, Long> {

    boolean existsByUserIdAndStatus(Long userId, WorkoutRequest.RequestStatus status);

    Optional<WorkoutRequest> findFirstByUserIdAndStatus(Long userId, WorkoutRequest.RequestStatus status);

    List<WorkoutRequest> findByStatusOrderByRequestedAtAsc(WorkoutRequest.RequestStatus status);

    List<WorkoutRequest> findByUserId(Long userId);
}
