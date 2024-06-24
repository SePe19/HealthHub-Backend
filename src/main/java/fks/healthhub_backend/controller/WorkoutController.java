package fks.healthhub_backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import fks.healthhub_backend.model.Workout;
import fks.healthhub_backend.model.WorkoutType;
import fks.healthhub_backend.service.WorkoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("api/workout")
public class WorkoutController {
    private final WorkoutService workoutService;

    @Autowired
    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getWorkout(@PathVariable Long id) {
        JsonNode workout = workoutService.getWorkout(id);
        return new ResponseEntity<>(workout, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Workout>> allWorkouts() {
        List<Workout> workouts = workoutService.getAllWorkouts();
        return new ResponseEntity<>(workouts, HttpStatus.OK);
    }

    @GetMapping("/user-workouts")
    public ResponseEntity<List<Workout>> allUserWorkouts(@RequestParam Long userId) {
        List<Workout> workouts = workoutService.getAllWorkoutsByUser(userId);
        return new ResponseEntity<>(workouts, HttpStatus.OK);
    }

    @GetMapping("/user-workouts-by-workout-type")
    public ResponseEntity<List<Workout>> workoutByWorkoutType(@RequestParam Long userId, @RequestParam WorkoutType workoutType) {
        List<Workout> workouts = workoutService.getAllWorkoutsByWorkoutType(userId, workoutType);
        return new ResponseEntity<>(workouts, HttpStatus.OK);
    }

    @GetMapping("/workout-types")
    public ResponseEntity<List<WorkoutType>> workoutTypes() {
        List<WorkoutType> workoutTypes = List.of(WorkoutType.values());
        return new ResponseEntity<>(workoutTypes, HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<Workout> createWorkout(@RequestBody Workout workout, @RequestParam Long userId) {
        Workout createdWorkout = workoutService.createWorkout(workout, userId);
        return new ResponseEntity<>(createdWorkout, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateWorkout(@PathVariable Long id, @RequestBody Workout workout, @RequestParam Long userId) {
        workoutService.updateWorkout(id, workout, userId);
        return ResponseEntity.ok().body(id);
    }

    @DeleteMapping("/{workoutId}/exercises/{exerciseId}")
    public ResponseEntity<Void> deleteWorkoutExercise(@PathVariable Long workoutId, @PathVariable Long exerciseId) {
        workoutService.deleteWorkoutExercise(workoutId, exerciseId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkout(@PathVariable Long id) {
        workoutService.deleteWorkout(id);
        return ResponseEntity.noContent().build();
    }
}
