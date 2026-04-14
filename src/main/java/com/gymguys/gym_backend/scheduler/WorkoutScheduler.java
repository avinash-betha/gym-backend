package com.gymguys.gym_backend.scheduler;

import com.gymguys.gym_backend.entity.DailyWorkout;
import com.gymguys.gym_backend.entity.User;
import com.gymguys.gym_backend.repository.DailyWorkoutRepository;
import com.gymguys.gym_backend.repository.UserRepository;
import com.gymguys.gym_backend.service.WorkoutGeneratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class WorkoutScheduler {

    private static final Logger log = LoggerFactory.getLogger(WorkoutScheduler.class);

    private final UserRepository userRepository;
    private final WorkoutGeneratorService generatorService;
    private final DailyWorkoutRepository workoutRepository;

    public WorkoutScheduler(UserRepository userRepository,
                            WorkoutGeneratorService generatorService,
                            DailyWorkoutRepository workoutRepository) {
        this.userRepository = userRepository;
        this.generatorService = generatorService;
        this.workoutRepository = workoutRepository;
    }

    @Scheduled(cron = "${scheduler.daily.workout}")
    public void generateDailyWorkout() {

        LocalDate today = LocalDate.now();
        DayOfWeek todayDay = today.getDayOfWeek();

        log.info("⏳ Scheduler triggered at: {} ({})", today, todayDay);

        List<User> users = userRepository.findAll();

        for (User user : users) {

            Long userId = user.getId();

            // ✅ Skip ADMIN
            if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                continue;
            }

            // ✅ Validate splitDays
            if (user.getSplitDays() == null || user.getSplitDays() <= 0) {
                log.warn("⚠ Skipping userId={} due to invalid splitDays", userId);
                continue;
            }

            int splitDays = user.getSplitDays();

            // ✅ Skip if already exists
            boolean alreadyExists = workoutRepository
                    .findByUserIdAndWorkoutDate(userId, today)
                    .isPresent();

            if (alreadyExists) {
                continue;
            }

            // 🔥 Calculate day number (1–7)
            int dayOfWeekNumber = todayDay.getValue(); // Mon=1 ... Sun=7

            log.info("📅 Today is day {} of week for userId={}", dayOfWeekNumber, userId);

            // 🚨 REST DAY LOGIC
            if (dayOfWeekNumber > splitDays) {

                DailyWorkout restWorkout = new DailyWorkout(
                        userId,
                        0, // 🔥 0 = REST DAY
                        "REST",
                        today
                );

                workoutRepository.save(restWorkout);

                log.info("🛌 Rest day assigned for userId={} on {}", userId, today);
                continue;
            }

            // ✅ WORKOUT DAY
            int workoutDay = dayOfWeekNumber;

            // Get last workout
            Optional<DailyWorkout> lastWorkoutOpt =
                    workoutRepository.findTopByUserIdOrderByWorkoutDateDesc(userId);

            String lastMuscles = lastWorkoutOpt
                    .map(DailyWorkout::getMuscleGroups)
                    .orElse("");

            List<String> todayMuscles = null;

            // Retry logic
            for (int i = 1; i <= 5; i++) {

                List<String> generated =
                        generatorService.generateTodayWorkout(splitDays);

                if (isDifferentWorkout(lastMuscles, generated)) {
                    todayMuscles = generated;
                    break;
                }
            }

            // fallback
            if (todayMuscles == null) {
                todayMuscles = generatorService.generateTodayWorkout(splitDays);
            }

            DailyWorkout workout = new DailyWorkout(
                    userId,
                    workoutDay,
                    String.join(",", todayMuscles),
                    today
            );

            workoutRepository.save(workout);

            log.info("💪 Workout saved → userId={} Day={} {}", userId, workoutDay, todayMuscles);
        }

        log.info("🎉 Scheduler completed");
    }

    private boolean isDifferentWorkout(String last, List<String> current) {

        if (last == null || last.isEmpty()) {
            return true;
        }

        List<String> lastList = Arrays.stream(last.split(","))
                .map(String::trim)
                .toList();

        for (String muscle : current) {
            if (lastList.contains(muscle)) {
                return false;
            }
        }

        return true;
    }
}