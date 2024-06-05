package fks.healthhub_backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import fks.healthhub_backend.model.Exercise;
import fks.healthhub_backend.model.MuscleGroup;
import fks.healthhub_backend.service.ExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("api/exercise")
public class ExerciseController {
    private final ExerciseService exerciseService;

    @Autowired
    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getExercise(@PathVariable Long id) {
        JsonNode exercise = exerciseService.getExercise(id);
        return new ResponseEntity<>(exercise, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Exercise>> allExercises() {
        List<Exercise> exercises = exerciseService.getAllExercises();
        return new ResponseEntity<>(exercises, HttpStatus.OK);
    }

    @GetMapping("/muscle-groups")
    public ResponseEntity<List<MuscleGroup>> muscleGroups() {
        List<MuscleGroup> muscleGroups = List.of(MuscleGroup.values());
        return new ResponseEntity<>(muscleGroups, HttpStatus.OK);
    }

    @GetMapping("/exercises-by-muscle-group")
    public ResponseEntity<List<Exercise>> getExercisesByMuscleGroup(@RequestParam MuscleGroup muscleGroup) {
        List<Exercise> exercises = exerciseService.getExercisesByMuscleGroup(muscleGroup);
        return new ResponseEntity<>(exercises, HttpStatus.OK);
    }

    @GetMapping("/exercises-by-title")
    public ResponseEntity<List<Exercise>> getExercisesByTitle(@RequestParam String title) {
        List<Exercise> exercises = exerciseService.getExercisesByTitle(title);
        return new ResponseEntity<>(exercises, HttpStatus.OK);
    }
}
