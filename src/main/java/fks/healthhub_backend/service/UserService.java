package fks.healthhub_backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fks.healthhub_backend.dto.UserDTO;
import fks.healthhub_backend.dto.UserHasWorkoutsDTO;
import fks.healthhub_backend.model.*;
import fks.healthhub_backend.repository.RecurringWorkoutRepository;
import fks.healthhub_backend.repository.UserHasWorkoutsRepository;
import fks.healthhub_backend.repository.UserRepository;
import fks.healthhub_backend.repository.WorkoutRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final WorkoutRepository workoutRepository;
    private final UserHasWorkoutsRepository userHasWorkoutsRepository;
    private final ObjectMapper objectMapper;
    private final RecurringWorkoutRepository recurringWorkoutRepository;

    @SneakyThrows
    public JsonNode getUser(Long id){
        User user = userRepository.findById(id).orElseThrow(()
                -> new NoResultException("User with id: " + id + " does not exist"));
        return objectMapper.valueToTree(user);
    }

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> new UserDTO(user.getId(), user.getUsername()))
                .collect(Collectors.toList());
    }

    @SneakyThrows
    public JsonNode getScheduledWorkouts(Long userId) {
        List<UserHasWorkouts> userWorkouts = userHasWorkoutsRepository.findByUserId(userId);
        return objectMapper.valueToTree(userWorkouts);
    }

    @SneakyThrows
    public JsonNode getScheduledWorkoutsForDate(Long userId, ZonedDateTime date) {
        ZonedDateTime startOfDay = date.toLocalDate().atStartOfDay(date.getZone());
        ZonedDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

        List<UserHasWorkouts> userWorkoutsForDate = userHasWorkoutsRepository.findByUserIdAndScheduledAtBetween(userId, startOfDay, endOfDay);
        return objectMapper.valueToTree(userWorkoutsForDate);
    }

    @SneakyThrows
    public JsonNode getScheduledWorkoutsForWeek(Long userId, ZonedDateTime date) {
        LocalDate localDate = date.toLocalDate();
        LocalDate startOfWeek = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        ZonedDateTime startOfWeekDateTime = startOfWeek.atStartOfDay(date.getZone());

        LocalDate endOfWeek = startOfWeek.plusDays(6);
        ZonedDateTime endOfWeekDateTime = endOfWeek.atTime(23, 59, 59).atZone(date.getZone());

        List<UserHasWorkouts> userWorkouts = userHasWorkoutsRepository.findByUserIdAndScheduledAtBetween(userId, startOfWeekDateTime, endOfWeekDateTime);
        return objectMapper.valueToTree(userWorkouts);
    }

    @SneakyThrows
    public JsonNode getRecurringWorkoutsForWeek(Long userId) {
        List<RecurringWorkout> recurringWorkouts = recurringWorkoutRepository.findByUserId(userId);
        return objectMapper.valueToTree(recurringWorkouts);
    }

    public List<Workout> getAllWorkoutsByUser(Long userId){
        return userRepository.findWorkoutsByUserId(userId);
    }

    public JsonNode getWorkoutCompletion(Long userId, int lookBackDays) {
        ZonedDateTime lookBackDate = ZonedDateTime.now().minusDays(lookBackDays);

        List<UserHasWorkouts> workouts = userHasWorkoutsRepository.findByUserIdAndScheduledAtAfter(userId, lookBackDate);
        int trueCount = 0;
        int falseCount = 0;

        for (UserHasWorkouts workout : workouts) {
            if (workout.getCompleted()) {
                trueCount++;
            } else {
                falseCount++;
            }
        }

        int totalCount = trueCount + falseCount;
        int percentage = (totalCount > 0) ? (int) Math.round(((double) trueCount / totalCount) * 100) : 0;

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.put("complete", trueCount);
        resultNode.put("incomplete", falseCount);
        resultNode.put("percentage", percentage);

        return resultNode;
    }

    public JsonNode getWorkoutFavourite(Long userId) {
        List<UserHasWorkouts> workouts = userHasWorkoutsRepository.findByUserId(userId);
        int strengthCount = 0;
        int cardioCount = 0;
        int mobilityCount = 0;

        for (UserHasWorkouts workout : workouts) {
            if (workout.getCompleted()) {
                WorkoutType type = workout.getWorkout().getWorkoutType();
                switch (type) {
                    case STRENGTH -> strengthCount++;
                    case CARDIO -> cardioCount++;
                    case MOBILITY -> mobilityCount++;
                }
            }
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.put("STRENGTH", strengthCount);
        resultNode.put("CARDIO", cardioCount);
        resultNode.put("MOBILITY", mobilityCount);

        return resultNode;
    }

    public void createUser(User user) {
        User existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser != null) {
            throw new IllegalArgumentException("Username: " + user.getUsername() + " is already taken");
        }
        userRepository.save(user);
    }

    public Long scheduleWorkout(Long userId, Long workoutId, ZonedDateTime scheduledAt) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new EntityNotFoundException("Workout not found with ID: " + workoutId));

        UserHasWorkouts userHasWorkout = new UserHasWorkouts();
        userHasWorkout.setUser(user);
        userHasWorkout.setWorkout(workout);
        userHasWorkout.setScheduledAt(scheduledAt);
        userHasWorkout.setCompleted(false);

        UserHasWorkouts savedWorkout = userHasWorkoutsRepository.save(userHasWorkout);
        return savedWorkout.getId();
    }

    public List<Long> scheduleRecurringWorkouts(Long userId, Long workoutId, Set<DayOfWeek> daysOfWeek, LocalTime timeOfDay) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new EntityNotFoundException("Workout not found with ID: " + workoutId));

        RecurringWorkout recurringWorkout = new RecurringWorkout();
        recurringWorkout.setUser(user);
        recurringWorkout.setWorkout(workout);
        recurringWorkout.setDaysOfWeek(daysOfWeek);
        recurringWorkout.setTimeOfDay(timeOfDay);

        recurringWorkoutRepository.save(recurringWorkout);

        return generateScheduledWorkoutsForRecurring(user, workout, recurringWorkout);
    }

    private List<Long> generateScheduledWorkoutsForRecurring(User user, Workout workout, RecurringWorkout recurringWorkout) {
        List<Long> scheduledWorkoutIds = new ArrayList<>();
        LocalDate startDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endDate = startDate.plusWeeks(12);
        ZoneId utcZone = ZoneId.of("UTC");

        for (DayOfWeek dayOfWeek : recurringWorkout.getDaysOfWeek()) {
            LocalDate date = startDate.with(TemporalAdjusters.nextOrSame(dayOfWeek));

            while (!date.isAfter(endDate)) {
                ZonedDateTime scheduledAt = date.atTime(recurringWorkout.getTimeOfDay()).atZone(utcZone);

                UserHasWorkouts scheduledWorkout = new UserHasWorkouts();
                scheduledWorkout.setUser(user);
                scheduledWorkout.setWorkout(workout);
                scheduledWorkout.setScheduledAt(scheduledAt);
                scheduledWorkout.setCompleted(false);

                UserHasWorkouts savedWorkout = userHasWorkoutsRepository.save(scheduledWorkout);
                scheduledWorkoutIds.add(savedWorkout.getId());

                date = date.plusWeeks(1);
            }
        }

        return scheduledWorkoutIds;
    }

    public void updateUser(Long id, User updatedUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoResultException("User with ID: " + id + " could not be found"));
        if(updatedUser != null) {
            user.setUsername(!updatedUser.getUsername().equals("") ? updatedUser.getUsername() : user.getUsername());
            user.setUpdatedAt(ZonedDateTime.now());
        }
        userRepository.save(user);
    }

    public void updateUserWorkout(Long userHasWorkoutsId, UserHasWorkoutsDTO updatedUserWorkout) {
        UserHasWorkouts userHasWorkouts = userHasWorkoutsRepository.findById(userHasWorkoutsId)
                .orElseThrow(() -> new NoResultException("UserHasWorkouts with ID: " + userHasWorkoutsId + " could not be found"));

        if (updatedUserWorkout != null) {
            userHasWorkouts.setScheduledAt(updatedUserWorkout.getScheduledAt());
            userHasWorkouts.setCompleted(updatedUserWorkout.getCompleted());
            userHasWorkouts.setUpdatedAt(ZonedDateTime.now());
        }

        userHasWorkoutsRepository.save(userHasWorkouts);
    }

    public void deleteScheduledWorkout(Long userHasWorkoutsId) {
        Optional<UserHasWorkouts> userHasWorkoutsOptional = userHasWorkoutsRepository.findById(userHasWorkoutsId);
        if (userHasWorkoutsOptional.isPresent()) {
            userHasWorkoutsRepository.delete(userHasWorkoutsOptional.get());
        } else {
            throw new NoResultException("Scheduled workout not found with ID: " + userHasWorkoutsId);
        }
    }

}
