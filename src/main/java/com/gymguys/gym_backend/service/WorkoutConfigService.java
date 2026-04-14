package com.gymguys.gym_backend.service;

import com.gymguys.gym_backend.entity.WorkoutConfig;
import com.gymguys.gym_backend.repository.WorkoutConfigRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkoutConfigService {

    private final WorkoutConfigRepository repository;

    public WorkoutConfigService(WorkoutConfigRepository repository) {
        this.repository = repository;
    }

    public WorkoutConfig save(WorkoutConfig config) {
        return repository.save(config);
    }

    public List<WorkoutConfig> getAll() {
        return repository.findAll();
    }
}