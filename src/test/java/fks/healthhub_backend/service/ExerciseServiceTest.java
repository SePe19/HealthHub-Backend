package fks.healthhub_backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fks.healthhub_backend.model.Exercise;
import fks.healthhub_backend.repository.ExerciseRepository;
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
}
