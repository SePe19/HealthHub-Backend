package fks.healthhub_backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fks.healthhub_backend.model.Workout;
import fks.healthhub_backend.repository.WorkoutHasExercisesRepository;
import fks.healthhub_backend.repository.ExerciseRepository;
import fks.healthhub_backend.repository.WorkoutRepository;
import jakarta.persistence.NoResultException;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkoutService {
    private final WorkoutRepository workoutRepository;
    private final ObjectMapper objectMapper;
    private final ExerciseRepository exerciseRepository;
    private final WorkoutHasExercisesRepository workoutHasExercisesRepository;

    @Autowired
    public WorkoutService(WorkoutRepository workoutRepository, ObjectMapper objectMapper,
                          @Qualifier("Exercise") ExerciseRepository exerciseRepository,
                          WorkoutHasExercisesRepository workoutHasExercisesRepository) {
        this.workoutRepository = workoutRepository;
        this.objectMapper = objectMapper;
        this.exerciseRepository = exerciseRepository;
        this.workoutHasExercisesRepository = workoutHasExercisesRepository;
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

    public void createWorkout(Workout workout) {
        workoutRepository.save(workout);
    }

    public void updateWorkout(Long id, Workout updatedWorkout) {
        Workout workout = workoutRepository.findById(id)
                .orElseThrow(() -> new NoResultException("Workout with ID: " + id + " could not be found"));
        if(updatedWorkout != null) {
            workout.setTitle(!updatedWorkout.getTitle().equals("") ? updatedWorkout.getTitle() : workout.getTitle());
            workout.setDescription(!updatedWorkout.getDescription().equals("") ? updatedWorkout.getDescription() : workout.getDescription());
        }
        workoutRepository.save(workout);
    }
}
