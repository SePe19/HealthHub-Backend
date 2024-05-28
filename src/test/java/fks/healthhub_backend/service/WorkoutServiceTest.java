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

class WorkoutServiceTest implements AutoCloseable {

    @Mock
    private WorkoutRepository workoutRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private WorkoutService workoutService;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @Override
    public void close() throws Exception {
        mocks.close();
    }

    @Test
    void getWorkout() {
        // Arrange
        Long workoutId = 1L;
        Workout workout = new Workout();
        workout.setId(workoutId);
        JsonNode workoutJson = mock(JsonNode.class);

        when(workoutRepository.findById(workoutId)).thenReturn(Optional.of(workout));
        when(objectMapper.valueToTree(workout)).thenReturn(workoutJson);

        // Act
        JsonNode result = workoutService.getWorkout(workoutId);

        // Assert
        assertNotNull(result);
        assertEquals(workoutJson, result);
        verify(workoutRepository, times(1)).findById(workoutId);
        verify(objectMapper, times(1)).valueToTree(workout);
    }

    @Test
    void getWorkout_notFound() {
        // Arrange
        Long workoutId = 1L;

        when(workoutRepository.findById(workoutId)).thenReturn(Optional.empty());

        // Act
        NoResultException exception = assertThrows(NoResultException.class, () -> workoutService.getWorkout(workoutId));

        // Assert
        assertEquals("Workout with id: 1 does not exist", exception.getMessage());
        verify(workoutRepository, times(1)).findById(workoutId);
        verify(objectMapper, never()).valueToTree(any());
    }

    @Test
    void getAllWorkouts() {
        // Arrange
        Workout workout1 = new Workout();
        Workout workout2 = new Workout();
        List<Workout> workouts = Arrays.asList(workout1, workout2);

        when(workoutRepository.findAll()).thenReturn(workouts);

        // Act
        List<Workout> result = workoutService.getAllWorkouts();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(workouts, result);
        verify(workoutRepository, times(1)).findAll();
    }

    @Test
    void getAllWorkoutsByUser() {
        // Arrange
        Long userId = 1L;
        Workout workout1 = new Workout();
        Workout workout2 = new Workout();
        List<Workout> workouts = Arrays.asList(workout1, workout2);

        when(workoutRepository.findWorkoutsByUserId(userId)).thenReturn(workouts);

        // Act
        List<Workout> result = workoutService.getAllWorkoutsByUser(userId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(workouts, result);
        verify(workoutRepository, times(1)).findWorkoutsByUserId(userId);
    }

    @Test
    void createWorkout() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        Workout workout = new Workout();
        Workout savedWorkout = new Workout();
        savedWorkout.setId(1L);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(workoutRepository.save(workout)).thenReturn(savedWorkout);

        // Act
        Workout result = workoutService.createWorkout(workout, userId);

        // Assert
        assertNotNull(result);
        assertEquals(savedWorkout, result);
        verify(userRepository, times(1)).findById(userId);
        verify(workoutRepository, times(1)).save(workout);
    }

    @Test
    void createWorkout_userNotFound() {
        // Arrange
        Long userId = 1L;
        Workout workout = new Workout();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        Exception exception = assertThrows(Exception.class, () -> workoutService.createWorkout(workout, userId));

        // Assert
        assertEquals("User not found with ID: 1", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(workoutRepository, never()).save(any());
    }

    @Test
    void updateWorkout() {
        // Arrange
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

        // Act
        workoutService.updateWorkout(workoutId, updatedWorkout, userId);

        // Assert
        assertEquals("New Title", existingWorkout.getTitle());
        assertEquals("New Description", existingWorkout.getDescription());
        assertEquals(user, existingWorkout.getUser());
        verify(workoutRepository, times(1)).findById(workoutId);
        verify(userRepository, times(1)).findById(userId);
        verify(workoutRepository, times(1)).save(existingWorkout);
    }

    @Test
    void updateWorkout_notFound() {
        // Arrange
        Long workoutId = 1L;
        Long userId = 1L;
        Workout updatedWorkout = new Workout();

        when(workoutRepository.findById(workoutId)).thenReturn(Optional.empty());

        // Act
        Exception exception = assertThrows(NoResultException.class, () -> workoutService.updateWorkout(workoutId, updatedWorkout, userId));

        // Assert
        assertEquals("Workout with ID: 1 could not be found", exception.getMessage());
        verify(workoutRepository, times(1)).findById(workoutId);
        verify(userRepository, never()).findById(userId);
        verify(workoutRepository, never()).save(any());
    }

    @Test
    void deleteWorkout() {
        // Arrange
        Long workoutId = 1L;

        doNothing().when(workoutRepository).deleteById(workoutId);

        // Act
        workoutService.deleteWorkout(workoutId);

        // Assert
        verify(workoutRepository, times(1)).deleteById(workoutId);
    }
}
