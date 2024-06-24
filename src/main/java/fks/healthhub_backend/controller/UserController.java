package fks.healthhub_backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import fks.healthhub_backend.dto.RecurringWorkoutDTO;
import fks.healthhub_backend.dto.UserAuthDTO;
import fks.healthhub_backend.dto.UserDTO;
import fks.healthhub_backend.dto.UserHasWorkoutsDTO;
import fks.healthhub_backend.model.User;
import fks.healthhub_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("api/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public void signup(@RequestBody UserAuthDTO userAuthDTO) {
        userService.signup(userAuthDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<Long> login(@RequestBody UserAuthDTO userAuthDTO) {
        User user = userService.login(userAuthDTO);
        if (user != null) {
            return ResponseEntity.ok(user.getId());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/logout")
    public void logout(@RequestParam Long userId) {
        userService.logout(userId);
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

    @GetMapping("/{id}/scheduled-workouts-for-date")
    public ResponseEntity<JsonNode> getScheduledWorkoutsForDate(@PathVariable Long id, @RequestParam("date") ZonedDateTime date) {
        JsonNode scheduledWorkoutsForDate = userService.getScheduledWorkoutsForDate(id, date);
        return new ResponseEntity<>(scheduledWorkoutsForDate, HttpStatus.OK);
    }

    @GetMapping("/{id}/scheduled-workouts-for-week")
    public ResponseEntity<JsonNode> getScheduledWorkoutsForWeek(@PathVariable Long id, @RequestParam("date") ZonedDateTime date) {
        JsonNode scheduledWorkoutsForWeek = userService.getScheduledWorkoutsForWeek(id, date);
        return new ResponseEntity<>(scheduledWorkoutsForWeek, HttpStatus.OK);
    }

    @GetMapping("/{id}/recurring-workouts-for-week")
    public ResponseEntity<JsonNode> getRecurringWorkoutsForWeek(@PathVariable Long id) {
        JsonNode recurringWorkoutsForWeek = userService.getRecurringWorkoutsForWeek(id);
        return new ResponseEntity<>(recurringWorkoutsForWeek, HttpStatus.OK);
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
    public ResponseEntity<Long> scheduleWorkout(@RequestBody UserHasWorkoutsDTO workout) {
        Long scheduledWorkoutId = userService.scheduleWorkout(
                workout.getUserId(),
                workout.getWorkoutId(),
                workout.getScheduledAt()
        );
        return new ResponseEntity<>(scheduledWorkoutId, HttpStatus.CREATED);
    }

    @PostMapping("/recurring-workouts")
    public ResponseEntity<List<Long>> scheduleRecurringWorkouts(@RequestBody RecurringWorkoutDTO recurringWorkout) {
        List<Long> scheduledWorkoutIds = userService.scheduleRecurringWorkouts(
                recurringWorkout.getUserId(),
                recurringWorkout.getWorkoutId(),
                recurringWorkout.getDaysOfWeek(),
                recurringWorkout.getTimeOfDay()
        );

        return new ResponseEntity<>(scheduledWorkoutIds, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateUser(@PathVariable Long id, @RequestBody User user) {
        userService.updateUser(id, user);
        return ResponseEntity.ok().body(id);
    }

    @PutMapping("/scheduled-workouts/{userHasWorkoutsId}")
    public ResponseEntity<UserHasWorkoutsDTO> updateUserWorkout(@PathVariable Long userHasWorkoutsId, @RequestBody UserHasWorkoutsDTO userHasWorkouts) {
        userService.updateUserWorkout(userHasWorkoutsId, userHasWorkouts);
        return ResponseEntity.ok().body(userHasWorkouts);
    }

    @DeleteMapping("/scheduled-workouts/{userHasWorkoutsId}")
    public ResponseEntity<Void> deleteScheduledWorkout(@PathVariable Long userHasWorkoutsId) {
        userService.deleteScheduledWorkout(userHasWorkoutsId);
        return ResponseEntity.noContent().build();
    }
}
