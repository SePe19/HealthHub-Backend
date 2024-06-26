package fks.healthhub_backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import fks.healthhub_backend.dto.UserDTO;
import fks.healthhub_backend.dto.UserHasWorkoutsDTO;
import fks.healthhub_backend.model.User;
import fks.healthhub_backend.model.UserHasWorkouts;
import fks.healthhub_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
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

    @GetMapping("/{id}/scheduled-workouts-for-week")
    public ResponseEntity<JsonNode> getScheduledWorkoutsForWeek(@PathVariable Long id, @RequestParam("date") ZonedDateTime date) {
        JsonNode scheduledWorkoutsForWeek = userService.getScheduledWorkoutsForWeek(id, date);
        return new ResponseEntity<>(scheduledWorkoutsForWeek, HttpStatus.OK);
    }

    @GetMapping("/{id}/workout-completion")
    public ResponseEntity<JsonNode> getWorkoutCompletion(@PathVariable Long id, @RequestParam(defaultValue = "91") int days) {
        JsonNode completion = userService.getWorkoutCompletion(id, days);
        return new ResponseEntity<>(completion, HttpStatus.OK);
    }

    @GetMapping("/{id}/workout-favourite")
    public ResponseEntity<JsonNode> getWorkoutFavourite(@PathVariable Long id) {
        JsonNode favourites = userService.getWorkoutFavourite(id);
        return new ResponseEntity<>(favourites, HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<Long> createUser(@RequestBody User user) {
        userService.createUser(user);
        return new ResponseEntity<>(user.getId(), HttpStatus.CREATED);
    }


    @PostMapping("/scheduled-workouts")
    public ResponseEntity<?> createScheduledWorkout(@RequestBody UserHasWorkoutsDTO workout) {
        Object result = userService.createScheduledWorkout(
                new UserHasWorkouts(),
                workout.getUserId(),
                workout.getWorkoutId(),
                workout.isRecurring(),
                workout.getDayOfWeek(),
                workout.getScheduledAt()
        );

        if (result instanceof List) {
            List<UserHasWorkouts> scheduledWorkouts = (List<UserHasWorkouts>) result;
            List<Long> ids = scheduledWorkouts.stream().map(UserHasWorkouts::getId).toList();
            return new ResponseEntity<>(ids, HttpStatus.CREATED);
        } else {
            UserHasWorkouts scheduledWorkout = (UserHasWorkouts) result;
            return new ResponseEntity<>(scheduledWorkout.getId(), HttpStatus.CREATED);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateUser(@PathVariable Long id, @RequestBody User user) {
        userService.updateUser(id, user);
        return ResponseEntity.ok().body(id);
    }

    @DeleteMapping("/scheduled-workouts/{userHasWorkoutsId}")
    public ResponseEntity<Void> deleteScheduledWorkout(@PathVariable Long userHasWorkoutsId) {
        userService.deleteScheduledWorkout(userHasWorkoutsId);
        return ResponseEntity.noContent().build();
    }

}
