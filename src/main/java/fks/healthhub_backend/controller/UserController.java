package fks.healthhub_backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import fks.healthhub_backend.dto.UserDTO;
import fks.healthhub_backend.model.User;
import fks.healthhub_backend.model.UserHasWorkouts;
import fks.healthhub_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("api/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        JsonNode user = userService.getUser(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}/scheduled-workouts")
    public ResponseEntity<JsonNode> getScheduledWorkout(@PathVariable Long id) {
        JsonNode workout = userService.getScheduledWorkouts(id);
        return new ResponseEntity<>(workout, HttpStatus.OK);
    }

    @GetMapping("/{id}/workout-completion")
    public ResponseEntity<JsonNode> getWorkoutCompletion(@PathVariable Long id) {
        JsonNode workout = userService.getWorkoutCompletion(id);
        return new ResponseEntity<>(workout, HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<Long> createUser(@RequestBody User user) {
        userService.createUser(user);
        return new ResponseEntity<>(user.getId(), HttpStatus.CREATED);
    }

    @PostMapping("/scheduled-workouts")
    public ResponseEntity<Long> createScheduledWorkout(@RequestBody UserHasWorkouts userHasWorkout, @RequestParam Long userId, @RequestParam Long workoutId) {
        UserHasWorkouts savedUserHasWorkout = userService.createScheduledWorkout(userHasWorkout, userId, workoutId);
        return new ResponseEntity<>(savedUserHasWorkout.getId(), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateUser(@PathVariable Long id, @RequestBody User user) {
        userService.updateUser(id, user);
        return ResponseEntity.ok().body(id);
    }
}
