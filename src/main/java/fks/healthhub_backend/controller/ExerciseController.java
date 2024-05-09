package fks.healthhub_backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import fks.healthhub_backend.model.Exercise;
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
}
