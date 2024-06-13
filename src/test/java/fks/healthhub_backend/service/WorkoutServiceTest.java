package fks.healthhub_backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import fks.healthhub_backend.dto.WorkoutDTO;
import fks.healthhub_backend.model.User;
import fks.healthhub_backend.model.Workout;
import fks.healthhub_backend.model.WorkoutHasExercises;
import fks.healthhub_backend.repository.UserRepository;
import fks.healthhub_backend.repository.WorkoutHasExercisesRepository;
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
    private WorkoutHasExercisesRepository workoutHasExercisesRepository;

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


        // Mocking the repository to return an Optional<WorkoutDTO> containing the workout DTO object
        when(workoutRepository.findById(workoutId)).thenReturn(Optional.of(workout));

        // Mocking the objectMapper to return a JsonNode
        when(objectMapper.valueToTree(any(WorkoutDTO.class))).thenAnswer(invocation -> {
            WorkoutDTO argument = invocation.getArgument(0);
            // Creating a JsonNode from the workoutDTO
            return objectMapper.valueToTree(argument);
        });

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
        Workout workout = new Workout(); // Create a workout object

        when(workoutRepository.findById(workoutId)).thenReturn(Optional.of(workout));

        // Act
        workoutService.deleteWorkout(workoutId);

        // Assert
        verify(workoutRepository, times(1)).delete(workout);
    }


    @Test
    void deleteWorkoutExercise_ExistingExercise_ShouldDelete() {
        Long workoutId = 1L;
        Long exerciseId = 1L;
        WorkoutHasExercises workoutHasExercises = new WorkoutHasExercises();
        workoutHasExercises.setId(1L);

        when(workoutHasExercisesRepository.findByWorkoutIdAndExerciseId(workoutId, exerciseId)).thenReturn(Optional.of(workoutHasExercises));

        workoutService.deleteWorkoutExercise(workoutId, exerciseId);

        verify(workoutHasExercisesRepository, times(1)).delete(workoutHasExercises);
    }

    @Test
    void deleteWorkoutExercise_NonExistingExercise_ShouldThrowException() {
        Long workoutId = 1L;
        Long exerciseId = 1L;

        when(workoutHasExercisesRepository.findByWorkoutIdAndExerciseId(workoutId, exerciseId)).thenReturn(Optional.empty());

        NoResultException exception = assertThrows(NoResultException.class, () -> workoutService.deleteWorkoutExercise(workoutId, exerciseId));
        assertEquals("Exercise with ID: 1 in Workout with ID: 1 could not be found", exception.getMessage());
    }

    @Test
    void deleteWorkout_ExistingWorkout_ShouldDelete() {
        Long workoutId = 1L;
        Workout workout = new Workout();
        workout.setId(workoutId);

        when(workoutRepository.findById(workoutId)).thenReturn(Optional.of(workout));

        workoutService.deleteWorkout(workoutId);

        verify(workoutRepository, times(1)).delete(workout);
    }

    @Test
    void deleteWorkout_NonExistingWorkout_ShouldThrowException() {
        Long workoutId = 1L;

        when(workoutRepository.findById(workoutId)).thenReturn(Optional.empty());

        NoResultException exception = assertThrows(NoResultException.class, () -> workoutService.deleteWorkout(workoutId));
        assertEquals("Workout with ID: 1 could not be found", exception.getMessage());
    }
}
