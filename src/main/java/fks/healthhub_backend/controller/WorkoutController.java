package fks.healthhub_backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import fks.healthhub_backend.model.Workout;
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

    @PostMapping("/")
    public ResponseEntity<Long> createWorkout(@RequestBody Workout workout) {
        workoutService.createWorkout(workout);
        return new ResponseEntity<>(workout.getId(), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateWorkout(@PathVariable Long id, @RequestBody Workout workout) {
        workoutService.updateWorkout(id, workout);
        return ResponseEntity.ok().body(id);
    }
}
