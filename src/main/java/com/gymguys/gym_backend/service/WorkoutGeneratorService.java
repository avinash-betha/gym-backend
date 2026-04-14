package com.gymguys.gym_backend.service;

import com.gymguys.gym_backend.entity.WorkoutConfig;
import com.gymguys.gym_backend.repository.WorkoutConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WorkoutGeneratorService {

    private static final Logger log = LoggerFactory.getLogger(WorkoutGeneratorService.class);

    private final WorkoutConfigRepository repository;
    private final Random random = new Random();

    public WorkoutGeneratorService(WorkoutConfigRepository repository) {
        this.repository = repository;
    }

    /**
     * Generate today's workout (1–2 muscle groups)
     */
    public List<String> generateTodayWorkout(int splitDays) {

        log.info("Generating today's workout for splitDays={}", splitDays);

        List<WorkoutConfig> configs = repository.findAll();

        if (configs.isEmpty()) {
            log.warn("No workout configs found in DB");
            return Collections.emptyList();
        }

        // Step 1: Extract all muscles
        List<String> allMuscles = extractMuscles(configs);

        if (allMuscles.isEmpty()) {
            log.warn("No muscle groups extracted from configs");
            return Collections.emptyList();
        }

        log.debug("Total muscle groups extracted: {}", allMuscles.size());

        // Step 2: Shuffle
        Collections.shuffle(allMuscles);

        // Step 3: Decide number of muscles (1–2)
        int count = random.nextInt(2) + 1;

        // Step 4: Random start index (IMPORTANT improvement)
        int startIndex = random.nextInt(allMuscles.size());

        List<String> todayMuscles = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            int index = (startIndex + i) % allMuscles.size();
            todayMuscles.add(allMuscles.get(index));
        }

        log.info("Generated today's workout: {}", todayMuscles);

        return todayMuscles;
    }

    /**
     * Extract muscle groups from DB configs
     */
    private List<String> extractMuscles(List<WorkoutConfig> configs) {

        List<String> muscles = new ArrayList<>();

        for (WorkoutConfig config : configs) {
            if (config.getMuscleGroup() == null) continue;

            String[] parts = config.getMuscleGroup().split(",");

            for (String p : parts) {
                String muscle = p.trim();

                if (!muscle.isEmpty()) {
                    muscles.add(muscle);
                }
            }
        }

        return muscles;
    }
}