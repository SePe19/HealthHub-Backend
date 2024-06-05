package fks.healthhub_backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fks.healthhub_backend.dto.UserDTO;
import fks.healthhub_backend.model.User;
import fks.healthhub_backend.model.UserHasWorkouts;
import fks.healthhub_backend.model.Workout;
import fks.healthhub_backend.model.WorkoutType;
import fks.healthhub_backend.repository.UserHasWorkoutsRepository;
import fks.healthhub_backend.repository.UserRepository;
import fks.healthhub_backend.repository.WorkoutRepository;
import jakarta.persistence.NoResultException;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final WorkoutRepository workoutRepository;
    private final UserHasWorkoutsRepository userHasWorkoutsRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserService(UserRepository userRepository, WorkoutRepository workoutRepository, UserHasWorkoutsRepository userHasWorkoutsRepository, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.workoutRepository = workoutRepository;
        this.userHasWorkoutsRepository = userHasWorkoutsRepository;
        this.objectMapper = objectMapper;
    }

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

    @SneakyThrows
    public UserHasWorkouts createScheduledWorkout(UserHasWorkouts userHasWorkout, Long userId, Long workoutId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found with ID: " + userId));
        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new Exception("Workout not found with ID: " + workoutId));

        userHasWorkout.setUser(user);
        userHasWorkout.setWorkout(workout);

        return userHasWorkoutsRepository.save(userHasWorkout);
    }

    public void updateUser(Long id, User updatedUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoResultException("User with ID: " + id + " could not be found"));
        if(updatedUser != null) {
            user.setUsername(!updatedUser.getUsername().equals("") ? updatedUser.getUsername() : user.getUsername());
        }
        userRepository.save(user);
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
