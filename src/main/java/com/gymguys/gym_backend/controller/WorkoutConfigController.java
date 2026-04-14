package com.gymguys.gym_backend.controller;

import com.gymguys.gym_backend.entity.WorkoutConfig;
import com.gymguys.gym_backend.service.WorkoutConfigService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/config")
public class WorkoutConfigController {

    private final WorkoutConfigService service;

    public WorkoutConfigController(WorkoutConfigService service) {
        this.service = service;
    }

    @PostMapping
    public WorkoutConfig addConfig(@RequestBody WorkoutConfig config) {
        return service.save(config);
    }

    @GetMapping
    public List<WorkoutConfig> getConfigs() {
        return service.getAll();
    }
}