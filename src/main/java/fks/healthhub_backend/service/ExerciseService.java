package fks.healthhub_backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fks.healthhub_backend.model.Exercise;
import fks.healthhub_backend.model.MuscleGroup;
import fks.healthhub_backend.repository.ExerciseRepository;
import jakarta.persistence.NoResultException;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public ExerciseService(ExerciseRepository exerciseRepository, ObjectMapper objectMapper) {
        this.exerciseRepository = exerciseRepository;
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    public JsonNode getExercise(Long id){
        Exercise exercise = exerciseRepository.findById(id).orElseThrow(()
                -> new NoResultException("Exercise with id: " + id + " does not exist"));
        return objectMapper.valueToTree(exercise);
    }

    public List<Exercise> getAllExercises(){
        return exerciseRepository.findAll();
    }

    public List<Exercise> getExercisesByMuscleGroup(MuscleGroup muscleGroup) {
        if (muscleGroup == null) {
            throw new IllegalArgumentException("Muscle group cannot be null");
        }
        return exerciseRepository.findAllByMuscleGroup(muscleGroup);
    }
}
