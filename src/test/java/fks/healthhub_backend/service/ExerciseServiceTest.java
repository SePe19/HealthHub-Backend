package fks.healthhub_backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fks.healthhub_backend.model.Exercise;
import fks.healthhub_backend.model.MuscleGroup;
import fks.healthhub_backend.repository.ExerciseRepository;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExerciseServiceTest implements AutoCloseable {

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ExerciseService exerciseService;

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
    void getExercise() {
        // Arrange
        Long exerciseId = 1L;
        Exercise exercise = new Exercise();
        exercise.setId(exerciseId);
        JsonNode exerciseJson = mock(JsonNode.class);

        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));
        when(objectMapper.valueToTree(exercise)).thenReturn(exerciseJson);

        // Act
        JsonNode result = exerciseService.getExercise(exerciseId);

        // Assert
        assertNotNull(result);
        assertEquals(exerciseJson, result);
        verify(exerciseRepository, times(1)).findById(exerciseId);
        verify(objectMapper, times(1)).valueToTree(exercise);
    }

    @Test
    void getExercise_notFound() {
        // Arrange
        Long exerciseId = 1L;

        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.empty());

        // Act
        NoResultException exception = assertThrows(NoResultException.class, () -> exerciseService.getExercise(exerciseId));

        // Assert
        assertEquals("Exercise with id: 1 does not exist", exception.getMessage());
        verify(exerciseRepository, times(1)).findById(exerciseId);
        verify(objectMapper, never()).valueToTree(any());
    }

    @Test
    void getAllExercises() {
        // Arrange
        Exercise exercise1 = new Exercise();
        Exercise exercise2 = new Exercise();
        List<Exercise> exercises = Arrays.asList(exercise1, exercise2);

        when(exerciseRepository.findAll()).thenReturn(exercises);

        // Act
        List<Exercise> result = exerciseService.getAllExercises();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(exercises, result);
        verify(exerciseRepository, times(1)).findAll();
    }

    @Test
    void getAllExercises_empty() {
        // Arrange
        when(exerciseRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Exercise> result = exerciseService.getAllExercises();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(exerciseRepository, times(1)).findAll();
    }

    @Test
    void getExercisesByMuscleGroup() {
        // Arrange
        MuscleGroup muscleGroup = MuscleGroup.CHEST;
        Exercise exercise1 = new Exercise();
        Exercise exercise2 = new Exercise();
        List<Exercise> exercises = Arrays.asList(exercise1, exercise2);

        when(exerciseRepository.findAllByMuscleGroup(muscleGroup)).thenReturn(exercises);

        // Act
        List<Exercise> result = exerciseService.getExercisesByMuscleGroup(muscleGroup);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(exercises, result);
        verify(exerciseRepository, times(1)).findAllByMuscleGroup(muscleGroup);
    }

    @Test
    void getExercisesByMuscleGroup_empty() {
        // Arrange
        MuscleGroup muscleGroup = MuscleGroup.CHEST;

        when(exerciseRepository.findAllByMuscleGroup(muscleGroup)).thenReturn(Collections.emptyList());

        // Act
        List<Exercise> result = exerciseService.getExercisesByMuscleGroup(muscleGroup);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(exerciseRepository, times(1)).findAllByMuscleGroup(muscleGroup);
    }

    @Test
    void getExercisesByMuscleGroup_invalidGroup() {
        // Arrange
        MuscleGroup muscleGroup = null;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> exerciseService.getExercisesByMuscleGroup(muscleGroup));
    }

    @Test
    void getExercisesByTitle() {
        // Arrange
        String title = "push";
        Exercise exercise1 = new Exercise();
        exercise1.setTitle("Push Up");
        Exercise exercise2 = new Exercise();
        exercise2.setTitle("Push Down");
        List<Exercise> exercises = Arrays.asList(exercise1, exercise2);

        when(exerciseRepository.findByTitleContaining(title)).thenReturn(exercises);

        // Act
        List<Exercise> result = exerciseService.getExercisesByTitle(title);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(exercises, result);
        verify(exerciseRepository, times(1)).findByTitleContaining(title);
    }

    @Test
    void getExercisesByTitle_noMatch() {
        // Arrange
        String title = "xyz";
        List<Exercise> exercises = Collections.emptyList();

        when(exerciseRepository.findByTitleContaining(title)).thenReturn(exercises);

        // Act
        List<Exercise> result = exerciseService.getExercisesByTitle(title);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        assertEquals(exercises, result);
        verify(exerciseRepository, times(1)).findByTitleContaining(title);
    }

    @Test
    void getExercisesByTitle_emptyString() {
        // Arrange
        String title = "";
        Exercise exercise1 = new Exercise();
        Exercise exercise2 = new Exercise();
        List<Exercise> exercises = Arrays.asList(exercise1, exercise2);

        when(exerciseRepository.findByTitleContaining(title)).thenReturn(exercises);

        // Act
        List<Exercise> result = exerciseService.getExercisesByTitle(title);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(exercises, result);
        verify(exerciseRepository, times(1)).findByTitleContaining(title);
    }
}
