package fks.healthhub_backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fks.healthhub_backend.model.User;
import fks.healthhub_backend.model.Workout;
import fks.healthhub_backend.repository.UserRepository;
import fks.healthhub_backend.repository.WorkoutHasExercisesRepository;
import fks.healthhub_backend.repository.ExerciseRepository;
import fks.healthhub_backend.repository.WorkoutRepository;
import jakarta.persistence.NoResultException;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkoutService {
    private final WorkoutRepository workoutRepository;
    private final ObjectMapper objectMapper;
    private final ExerciseRepository exerciseRepository;
    private final WorkoutHasExercisesRepository workoutHasExercisesRepository;
    private final UserRepository userRepository;

    @Autowired
    public WorkoutService(WorkoutRepository workoutRepository, ObjectMapper objectMapper,
                          ExerciseRepository exerciseRepository,
                          WorkoutHasExercisesRepository workoutHasExercisesRepository, UserRepository userRepository) {
        this.workoutRepository = workoutRepository;
        this.objectMapper = objectMapper;
        this.exerciseRepository = exerciseRepository;
        this.workoutHasExercisesRepository = workoutHasExercisesRepository;
        this.userRepository = userRepository;
    }

    @SneakyThrows
    public JsonNode getWorkout(Long id){
        Workout workout = workoutRepository.findById(id).orElseThrow(()
                -> new NoResultException("Workout with id: " + id + " does not exist"));
        return objectMapper.valueToTree(workout);
    }

    public List<Workout> getAllWorkouts(){
        List<Workout> workouts = workoutRepository.findAll();
        return workouts;
    }

    public List<Workout> getAllWorkoutsByUser(Long userId){
        List<Workout> workouts = workoutRepository.findWorkoutsByUserId(userId);
        return workouts;
    }

    @SneakyThrows
    public Workout createWorkout(Workout workout, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found with ID: " + userId));
        workout.setUser(user);
        return workoutRepository.save(workout);
    }

    @SneakyThrows
    public void updateWorkout(Long id, Workout updatedWorkout, Long userId) {
        Workout workout = workoutRepository.findById(id)
                .orElseThrow(() -> new NoResultException("Workout with ID: " + id + " could not be found"));
        if(updatedWorkout != null) {
            workout.setTitle(!updatedWorkout.getTitle().equals("") ? updatedWorkout.getTitle() : workout.getTitle());
            workout.setDescription(!updatedWorkout.getDescription().equals("") ? updatedWorkout.getDescription() : workout.getDescription());

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new Exception("User not found with ID: " + userId));
            workout.setUser(user);
        }
        workoutRepository.save(workout);
    }

    public void deleteWorkout(Long id) {
        workoutRepository.deleteById(id);
    }
}
