package fks.healthhub_backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fks.healthhub_backend.model.Workout;
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

    @Autowired
    public WorkoutService(WorkoutRepository workoutRepository, ObjectMapper objectMapper) {
        this.workoutRepository = workoutRepository;
        this.objectMapper = objectMapper;
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
}
