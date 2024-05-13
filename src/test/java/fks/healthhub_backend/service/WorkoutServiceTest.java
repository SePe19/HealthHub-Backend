package fks.healthhub_backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fks.healthhub_backend.model.User;
import fks.healthhub_backend.model.Workout;
import fks.healthhub_backend.repository.UserRepository;
import fks.healthhub_backend.repository.WorkoutRepository;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WorkoutServiceTest {

    @Mock
    private WorkoutRepository workoutRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private WorkoutService workoutService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getWorkout() {
        Long workoutId = 1L;
        Workout workout = new Workout();
        workout.setId(workoutId);
        JsonNode workoutJson = mock(JsonNode.class);

        when(workoutRepository.findById(workoutId)).thenReturn(Optional.of(workout));
        when(objectMapper.valueToTree(workout)).thenReturn(workoutJson);

        JsonNode result = workoutService.getWorkout(workoutId);

        assertNotNull(result);
        assertEquals(workoutJson, result);
        verify(workoutRepository, times(1)).findById(workoutId);
        verify(objectMapper, times(1)).valueToTree(workout);
    }

    @Test
    void getWorkout_notFound() {
        Long workoutId = 1L;

        when(workoutRepository.findById(workoutId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NoResultException.class, () -> {
            workoutService.getWorkout(workoutId);
        });

        assertEquals("Workout with id: 1 does not exist", exception.getMessage());
        verify(workoutRepository, times(1)).findById(workoutId);
        verify(objectMapper, never()).valueToTree(any());
    }

    @Test
    void getAllWorkouts() {
        Workout workout1 = new Workout();
        Workout workout2 = new Workout();
        List<Workout> workouts = Arrays.asList(workout1, workout2);

        when(workoutRepository.findAll()).thenReturn(workouts);

        List<Workout> result = workoutService.getAllWorkouts();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(workouts, result);
        verify(workoutRepository, times(1)).findAll();
    }

    @Test
    void getAllWorkoutsByUser() {
        Long userId = 1L;
        Workout workout1 = new Workout();
        Workout workout2 = new Workout();
        List<Workout> workouts = Arrays.asList(workout1, workout2);

        when(workoutRepository.findWorkoutsByUserId(userId)).thenReturn(workouts);

        List<Workout> result = workoutService.getAllWorkoutsByUser(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(workouts, result);
        verify(workoutRepository, times(1)).findWorkoutsByUserId(userId);
    }

    @Test
    void createWorkout() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        Workout workout = new Workout();
        Workout savedWorkout = new Workout();
        savedWorkout.setId(1L);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(workoutRepository.save(workout)).thenReturn(savedWorkout);

        Workout result = workoutService.createWorkout(workout, userId);

        assertNotNull(result);
        assertEquals(savedWorkout, result);
        verify(userRepository, times(1)).findById(userId);
        verify(workoutRepository, times(1)).save(workout);
    }

    @Test
    void createWorkout_userNotFound() {
        Long userId = 1L;
        Workout workout = new Workout();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            workoutService.createWorkout(workout, userId);
        });

        assertEquals("User not found with ID: 1", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(workoutRepository, never()).save(any());
    }

    @Test
    void updateWorkout() {
        Long workoutId = 1L;
        Long userId = 1L;
        Workout existingWorkout = new Workout();
        existingWorkout.setId(workoutId);
        User user = new User();
        user.setId(userId);
        Workout updatedWorkout = new Workout();
        updatedWorkout.setTitle("New Title");
        updatedWorkout.setDescription("New Description");

        when(workoutRepository.findById(workoutId)).thenReturn(Optional.of(existingWorkout));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        workoutService.updateWorkout(workoutId, updatedWorkout, userId);

        assertEquals("New Title", existingWorkout.getTitle());
        assertEquals("New Description", existingWorkout.getDescription());
        assertEquals(user, existingWorkout.getUser());
        verify(workoutRepository, times(1)).findById(workoutId);
        verify(userRepository, times(1)).findById(userId);
        verify(workoutRepository, times(1)).save(existingWorkout);
    }

    @Test
    void updateWorkout_notFound() {
        Long workoutId = 1L;
        Long userId = 1L;
        Workout updatedWorkout = new Workout();

        when(workoutRepository.findById(workoutId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NoResultException.class, () -> {
            workoutService.updateWorkout(workoutId, updatedWorkout, userId);
        });

        assertEquals("Workout with ID: 1 could not be found", exception.getMessage());
        verify(workoutRepository, times(1)).findById(workoutId);
        verify(userRepository, never()).findById(userId);
        verify(workoutRepository, never()).save(any());
    }

    @Test
    void deleteWorkout() {
        Long workoutId = 1L;

        doNothing().when(workoutRepository).deleteById(workoutId);

        workoutService.deleteWorkout(workoutId);

        verify(workoutRepository, times(1)).deleteById(workoutId);
    }
}
