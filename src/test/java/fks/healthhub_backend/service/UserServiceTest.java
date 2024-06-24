package fks.healthhub_backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fks.healthhub_backend.dto.UserDTO;
import fks.healthhub_backend.model.User;
import fks.healthhub_backend.model.UserHasWorkouts;
import fks.healthhub_backend.model.Workout;
import fks.healthhub_backend.model.WorkoutType;
import fks.healthhub_backend.repository.RecurringWorkoutRepository;
import fks.healthhub_backend.repository.UserHasWorkoutsRepository;
import fks.healthhub_backend.repository.UserRepository;
import fks.healthhub_backend.repository.WorkoutRepository;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest implements AutoCloseable {

    @Mock
    private UserRepository userRepository;

    @Mock
    private WorkoutRepository workoutRepository;

    @Mock
    private UserHasWorkoutsRepository userHasWorkoutsRepository;

    @Mock
    private RecurringWorkoutRepository recurringWorkoutRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private UserService userService;

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
    void getUser() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        JsonNode userJson = mock(JsonNode.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(objectMapper.valueToTree(user)).thenReturn(userJson);

        // Act
        JsonNode result = userService.getUser(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userJson, result);
        verify(userRepository, times(1)).findById(userId);
        verify(objectMapper, times(1)).valueToTree(user);
    }

    @Test
    void getUser_notFound() {
        // Arrange
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        NoResultException exception = assertThrows(NoResultException.class, () -> userService.getUser(userId));

        // Assert
        assertEquals("User with id: 1 does not exist", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(objectMapper, never()).valueToTree(any());
    }

    @Test
    void getAllUsers() {
        // Arrange
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");

        List<User> users = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<UserDTO> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getUsername());
        assertEquals("user2", result.get(1).getUsername());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getScheduledWorkouts() {
        // Arrange
        Long userId = 1L;
        UserHasWorkouts userHasWorkout1 = new UserHasWorkouts();
        UserHasWorkouts userHasWorkout2 = new UserHasWorkouts();
        List<UserHasWorkouts> userHasWorkouts = Arrays.asList(userHasWorkout1, userHasWorkout2);
        JsonNode userHasWorkoutsJson = mock(JsonNode.class);

        when(userHasWorkoutsRepository.findByUserId(userId)).thenReturn(userHasWorkouts);
        when(objectMapper.valueToTree(userHasWorkouts)).thenReturn(userHasWorkoutsJson);

        // Act
        JsonNode result = userService.getScheduledWorkouts(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userHasWorkoutsJson, result);
        verify(userHasWorkoutsRepository, times(1)).findByUserId(userId);
        verify(objectMapper, times(1)).valueToTree(userHasWorkouts);
    }

    @Test
    void getScheduledWorkoutsForWeek_WeekWithWorkouts() {
        // Arrange
        Long userId = 1L;
        ZonedDateTime date = ZonedDateTime.now();
        LocalDate startOfWeek = date.toLocalDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        ZonedDateTime startOfWeekDateTime = startOfWeek.atStartOfDay(date.getZone());
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        ZonedDateTime endOfWeekDateTime = endOfWeek.atTime(23, 59, 59).atZone(date.getZone());
        List<UserHasWorkouts> userWorkouts = Arrays.asList(new UserHasWorkouts(), new UserHasWorkouts());
        when(userHasWorkoutsRepository.findByUserIdAndScheduledAtBetween(userId, startOfWeekDateTime, endOfWeekDateTime))
                .thenReturn(userWorkouts);

        // Act
        JsonNode result = userService.getScheduledWorkoutsForWeek(userId, date);

        // Assert
        assertEquals(objectMapper.valueToTree(userWorkouts), result);
        verify(userHasWorkoutsRepository, times(1)).findByUserIdAndScheduledAtBetween(userId, startOfWeekDateTime, endOfWeekDateTime);
        verifyNoMoreInteractions(userHasWorkoutsRepository);
    }

    @Test
    void getScheduledWorkoutsForWeek_WeekWithPartialWorkouts() {
        // Arrange
        Long userId = 1L;
        ZonedDateTime date = ZonedDateTime.of(2024, 6, 6, 0, 0, 0, 0, ZoneId.systemDefault());
        LocalDate startOfWeek = date.toLocalDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        ZonedDateTime startOfWeekDateTime = startOfWeek.atStartOfDay(date.getZone());
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        ZonedDateTime endOfWeekDateTime = endOfWeek.atTime(23, 59, 59).atZone(date.getZone());
        List<UserHasWorkouts> userWorkouts = Arrays.asList(new UserHasWorkouts(), new UserHasWorkouts());
        when(userHasWorkoutsRepository.findByUserIdAndScheduledAtBetween(userId, startOfWeekDateTime, endOfWeekDateTime))
                .thenReturn(userWorkouts);

        // Act
        JsonNode result = userService.getScheduledWorkoutsForWeek(userId, date);

        // Assert
        assertEquals(objectMapper.valueToTree(userWorkouts), result);
        verify(userHasWorkoutsRepository, times(1)).findByUserIdAndScheduledAtBetween(userId, startOfWeekDateTime, endOfWeekDateTime);
        verifyNoMoreInteractions(userHasWorkoutsRepository);
    }

    @Test
    void getScheduledWorkoutsForWeek_WeekWithoutWorkouts() {
        // Arrange
        Long userId = 1L;
        ZonedDateTime date = ZonedDateTime.of(2024, 6, 6, 0, 0, 0, 0, ZoneId.systemDefault());
        LocalDate startOfWeek = date.toLocalDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        ZonedDateTime startOfWeekDateTime = startOfWeek.atStartOfDay(date.getZone());
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        ZonedDateTime endOfWeekDateTime = endOfWeek.atTime(23, 59, 59).atZone(date.getZone());
        List<UserHasWorkouts> userWorkouts = null;
        when(userHasWorkoutsRepository.findByUserIdAndScheduledAtBetween(userId, startOfWeekDateTime, endOfWeekDateTime))
                .thenReturn(userWorkouts);

        // Act
        JsonNode result = userService.getScheduledWorkoutsForWeek(userId, date);

        // Assert
        assertNull(result);
        verify(userHasWorkoutsRepository, times(1)).findByUserIdAndScheduledAtBetween(userId, startOfWeekDateTime, endOfWeekDateTime);
        verifyNoMoreInteractions(userHasWorkoutsRepository);
    }

    @Test
    void getScheduledWorkoutsForWeek_DateOnFirstDayOfWeek() {
        // Arrange
        Long userId = 1L;
        ZonedDateTime date = ZonedDateTime.of(2024, 6, 3, 0, 0, 0, 0, ZoneId.systemDefault());
        LocalDate startOfWeek = date.toLocalDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        ZonedDateTime startOfWeekDateTime = startOfWeek.atStartOfDay(date.getZone());
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        ZonedDateTime endOfWeekDateTime = endOfWeek.atTime(23, 59, 59).atZone(date.getZone());
        List<UserHasWorkouts> userWorkouts = Arrays.asList(new UserHasWorkouts(), new UserHasWorkouts());
        when(userHasWorkoutsRepository.findByUserIdAndScheduledAtBetween(userId, startOfWeekDateTime, endOfWeekDateTime))
                .thenReturn(userWorkouts);

        // Act
        JsonNode result = userService.getScheduledWorkoutsForWeek(userId, date);

        // Assert
        assertEquals(objectMapper.valueToTree(userWorkouts), result);
        verify(userHasWorkoutsRepository, times(1)).findByUserIdAndScheduledAtBetween(userId, startOfWeekDateTime, endOfWeekDateTime);
        verifyNoMoreInteractions(userHasWorkoutsRepository);
    }

    @Test
    void getScheduledWorkoutsForWeek_DateOnLastDayOfWeek() {
        // Arrange
        Long userId = 1L;
        ZonedDateTime date = ZonedDateTime.of(2024, 6, 9, 0, 0, 0, 0, ZoneId.systemDefault());
        LocalDate startOfWeek = date.toLocalDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        ZonedDateTime startOfWeekDateTime = startOfWeek.atStartOfDay(date.getZone());
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        ZonedDateTime endOfWeekDateTime = endOfWeek.atTime(23, 59, 59).atZone(date.getZone());
        List<UserHasWorkouts> userWorkouts = Arrays.asList(new UserHasWorkouts(), new UserHasWorkouts());
        when(userHasWorkoutsRepository.findByUserIdAndScheduledAtBetween(userId, startOfWeekDateTime, endOfWeekDateTime))
                .thenReturn(userWorkouts);

        // Act
        JsonNode result = userService.getScheduledWorkoutsForWeek(userId, date);

        // Assert
        assertEquals(objectMapper.valueToTree(userWorkouts), result);
        verify(userHasWorkoutsRepository, times(1)).findByUserIdAndScheduledAtBetween(userId, startOfWeekDateTime, endOfWeekDateTime);
        verifyNoMoreInteractions(userHasWorkoutsRepository);
    }

    @Test
    void getAllWorkoutsByUser() {
        // Arrange
        Long userId = 1L;
        Workout workout1 = new Workout();
        Workout workout2 = new Workout();
        List<Workout> workouts = Arrays.asList(workout1, workout2);

        when(userRepository.findWorkoutsByUserId(userId)).thenReturn(workouts);

        // Act
        List<Workout> result = userService.getAllWorkoutsByUser(userId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(workouts, result);
        verify(userRepository, times(1)).findWorkoutsByUserId(userId);
    }

    @Test
    void getWorkoutCompletion() {
        Long userId = 1L;
        UserHasWorkouts workout1 = new UserHasWorkouts();
        workout1.setCompleted(true);
        workout1.setScheduledAt(ZonedDateTime.now().minusDays(10));
        UserHasWorkouts workout2 = new UserHasWorkouts();
        workout2.setCompleted(false);
        workout2.setScheduledAt(ZonedDateTime.now().minusDays(20));
        UserHasWorkouts workout3 = new UserHasWorkouts();
        workout3.setCompleted(true);
        workout3.setScheduledAt(ZonedDateTime.now().minusDays(30));

        List<UserHasWorkouts> workouts = Arrays.asList(workout1, workout2, workout3);

        when(userHasWorkoutsRepository.findByUserIdAndScheduledAtAfter(eq(userId), any(ZonedDateTime.class))).thenReturn(workouts);

        JsonNode result = userService.getWorkoutCompletion(userId, 91);

        assertNotNull(result);
        assertEquals(2, result.get("complete").asInt());
        assertEquals(1, result.get("incomplete").asInt());
        assertEquals(67, result.get("percentage").asInt());

        verify(userHasWorkoutsRepository, times(1)).findByUserIdAndScheduledAtAfter(eq(userId), any(ZonedDateTime.class));
    }

    @Test
    void getWorkoutCompletion_noCompletedWorkouts() {
        Long userId = 2L;
        UserHasWorkouts workout1 = new UserHasWorkouts();
        workout1.setCompleted(false);
        workout1.setScheduledAt(ZonedDateTime.now().minusDays(10));
        UserHasWorkouts workout2 = new UserHasWorkouts();
        workout2.setCompleted(false);
        workout2.setScheduledAt(ZonedDateTime.now().minusDays(20));

        List<UserHasWorkouts> workouts = Arrays.asList(workout1, workout2);

        when(userHasWorkoutsRepository.findByUserIdAndScheduledAtAfter(eq(userId), any(ZonedDateTime.class))).thenReturn(workouts);

        JsonNode result = userService.getWorkoutCompletion(userId, 91);

        assertNotNull(result);
        assertEquals(0, result.get("complete").asInt());
        assertEquals(2, result.get("incomplete").asInt());
        assertEquals(0, result.get("percentage").asInt());

        verify(userHasWorkoutsRepository, times(1)).findByUserIdAndScheduledAtAfter(eq(userId), any(ZonedDateTime.class));
    }

    @Test
    void getWorkoutCompletion_noIncompleteWorkouts() {
        Long userId = 3L;
        UserHasWorkouts workout1 = new UserHasWorkouts();
        workout1.setCompleted(true);
        workout1.setScheduledAt(ZonedDateTime.now().minusDays(10));
        UserHasWorkouts workout2 = new UserHasWorkouts();
        workout2.setCompleted(true);
        workout2.setScheduledAt(ZonedDateTime.now().minusDays(20));

        List<UserHasWorkouts> workouts = Arrays.asList(workout1, workout2);

        when(userHasWorkoutsRepository.findByUserIdAndScheduledAtAfter(eq(userId), any(ZonedDateTime.class))).thenReturn(workouts);

        JsonNode result = userService.getWorkoutCompletion(userId, 91);

        assertNotNull(result);
        assertEquals(2, result.get("complete").asInt());
        assertEquals(0, result.get("incomplete").asInt());
        assertEquals(100, result.get("percentage").asInt());

        verify(userHasWorkoutsRepository, times(1)).findByUserIdAndScheduledAtAfter(eq(userId), any(ZonedDateTime.class));
    }

    @Test
    void getWorkoutCompletion_noWorkouts() {
        Long userId = 4L;

        when(userHasWorkoutsRepository.findByUserIdAndScheduledAtAfter(eq(userId), any(ZonedDateTime.class))).thenReturn(List.of());

        JsonNode result = userService.getWorkoutCompletion(userId, 91);

        assertNotNull(result);
        assertEquals(0, result.get("complete").asInt());
        assertEquals(0, result.get("incomplete").asInt());
        assertEquals(0, result.get("percentage").asInt());

        verify(userHasWorkoutsRepository, times(1)).findByUserIdAndScheduledAtAfter(eq(userId), any(ZonedDateTime.class));
    }


    @Test
    void getWorkoutFavourite_allTypesPresent() {
        // Arrange
        Long userId = 1L;
        UserHasWorkouts workout1 = new UserHasWorkouts();
        workout1.setCompleted(true);
        Workout workoutType1 = new Workout();
        workoutType1.setWorkoutType(WorkoutType.STRENGTH);
        workout1.setWorkout(workoutType1);

        UserHasWorkouts workout2 = new UserHasWorkouts();
        workout2.setCompleted(true);
        Workout workoutType2 = new Workout();
        workoutType2.setWorkoutType(WorkoutType.CARDIO);
        workout2.setWorkout(workoutType2);

        UserHasWorkouts workout3 = new UserHasWorkouts();
        workout3.setCompleted(true);
        Workout workoutType3 = new Workout();
        workoutType3.setWorkoutType(WorkoutType.MOBILITY);
        workout3.setWorkout(workoutType3);

        List<UserHasWorkouts> workouts = Arrays.asList(workout1, workout2, workout3);

        when(userHasWorkoutsRepository.findByUserId(userId)).thenReturn(workouts);

        // Act
        JsonNode result = userService.getWorkoutFavourite(userId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.get("STRENGTH").asInt());
        assertEquals(1, result.get("CARDIO").asInt());
        assertEquals(1, result.get("MOBILITY").asInt());

        verify(userHasWorkoutsRepository, times(1)).findByUserId(userId);
    }

    @Test
    void getWorkoutFavourite_noCompletedWorkouts() {
        // Arrange
        Long userId = 2L;
        UserHasWorkouts workout1 = new UserHasWorkouts();
        workout1.setCompleted(false);
        Workout workoutType1 = new Workout();
        workoutType1.setWorkoutType(WorkoutType.STRENGTH);
        workout1.setWorkout(workoutType1);

        UserHasWorkouts workout2 = new UserHasWorkouts();
        workout2.setCompleted(false);
        Workout workoutType2 = new Workout();
        workoutType2.setWorkoutType(WorkoutType.CARDIO);
        workout2.setWorkout(workoutType2);

        List<UserHasWorkouts> workouts = Arrays.asList(workout1, workout2);

        when(userHasWorkoutsRepository.findByUserId(userId)).thenReturn(workouts);

        // Act
        JsonNode result = userService.getWorkoutFavourite(userId);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.get("STRENGTH").asInt());
        assertEquals(0, result.get("CARDIO").asInt());
        assertEquals(0, result.get("MOBILITY").asInt());

        verify(userHasWorkoutsRepository, times(1)).findByUserId(userId);
    }

    @Test
    void getWorkoutFavourite_mixedWorkoutTypes() {
        // Arrange
        Long userId = 3L;
        UserHasWorkouts workout1 = new UserHasWorkouts();
        workout1.setCompleted(true);
        Workout workoutType1 = new Workout();
        workoutType1.setWorkoutType(WorkoutType.STRENGTH);
        workout1.setWorkout(workoutType1);

        UserHasWorkouts workout2 = new UserHasWorkouts();
        workout2.setCompleted(true);
        Workout workoutType2 = new Workout();
        workoutType2.setWorkoutType(WorkoutType.STRENGTH);
        workout2.setWorkout(workoutType2);

        UserHasWorkouts workout3 = new UserHasWorkouts();
        workout3.setCompleted(true);
        Workout workoutType3 = new Workout();
        workoutType3.setWorkoutType(WorkoutType.MOBILITY);
        workout3.setWorkout(workoutType3);

        List<UserHasWorkouts> workouts = Arrays.asList(workout1, workout2, workout3);

        when(userHasWorkoutsRepository.findByUserId(userId)).thenReturn(workouts);

        // Act
        JsonNode result = userService.getWorkoutFavourite(userId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.get("STRENGTH").asInt());
        assertEquals(0, result.get("CARDIO").asInt());
        assertEquals(1, result.get("MOBILITY").asInt());

        verify(userHasWorkoutsRepository, times(1)).findByUserId(userId);
    }

    @Test
    void getWorkoutFavourite_noWorkouts() {
        // Arrange
        Long userId = 4L;

        when(userHasWorkoutsRepository.findByUserId(userId)).thenReturn(List.of());

        // Act
        JsonNode result = userService.getWorkoutFavourite(userId);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.get("STRENGTH").asInt());
        assertEquals(0, result.get("CARDIO").asInt());
        assertEquals(0, result.get("MOBILITY").asInt());

        verify(userHasWorkoutsRepository, times(1)).findByUserId(userId);
    }

    @Test
    void createUser() {
        // Arrange
        User user = new User();
        user.setUsername("testUser");

        when(userRepository.findByUsername("testUser")).thenReturn(null);
        when(userRepository.save(user)).thenReturn(user);

        // Act
        userService.createUser(user);

        // Assert
        verify(userRepository, times(1)).findByUsername("testUser");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void createUser_usernameTaken() {
        // Arrange
        User user = new User();
        user.setUsername("testUser");
        User existingUser = new User();
        existingUser.setUsername("testUser");

        when(userRepository.findByUsername("testUser")).thenReturn(existingUser);

        // Act
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));

        // Assert
        assertEquals("Username: testUser is already taken", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("testUser");
        verify(userRepository, never()).save(any());
    }

    @Test
    void scheduleWorkout() {
        // Arrange
        Long userId = 1L;
        Long workoutId = 1L;
        ZonedDateTime scheduledAt = ZonedDateTime.now();

        // Mocked instance of UserHasWorkouts
        UserHasWorkouts mockedSavedWorkout = new UserHasWorkouts();
        mockedSavedWorkout.setId(1L); // Set an ID to avoid NPE on getId()

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(workoutRepository.findById(workoutId)).thenReturn(Optional.of(new Workout()));
        when(userHasWorkoutsRepository.save(any(UserHasWorkouts.class))).thenReturn(mockedSavedWorkout); // Ensure save returns a mock instance

        // Act & Assert
        assertDoesNotThrow(() -> userService.scheduleWorkout(userId, workoutId, scheduledAt));

        verify(userHasWorkoutsRepository).save(any(UserHasWorkouts.class));
    }

    @Test
    void scheduleRecurringWorkout() {
        // Arrange
        Long userId = 1L;
        Long workoutId = 1L;
        Set<DayOfWeek> dayOfWeekSet = new HashSet<>();
        dayOfWeekSet.add(DayOfWeek.MONDAY);
        dayOfWeekSet.add(DayOfWeek.FRIDAY);
        LocalTime timeOfDay = LocalTime.now();

        User user = new User();
        Workout workout = new Workout();
        List<UserHasWorkouts> scheduledWorkouts = new ArrayList<>();
        UserHasWorkouts mockedSavedWorkout = new UserHasWorkouts();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(workoutRepository.findById(workoutId)).thenReturn(Optional.of(workout));
        when(userHasWorkoutsRepository.save(any(UserHasWorkouts.class))).thenReturn(mockedSavedWorkout);

        // Act
        List<Long> result = userService.scheduleRecurringWorkouts(userId, workoutId, dayOfWeekSet, timeOfDay);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof List);
        assertEquals(25, result.size());

        verify(userHasWorkoutsRepository, times(dayOfWeekSet.size() * 12 + 1)).save(any(UserHasWorkouts.class));
    }

    @Test
    void createScheduledWorkout_userNotFound() {
        // Arrange
        Long userId = 1L;
        Long workoutId = 1L;
        ZonedDateTime scheduledAt = ZonedDateTime.now();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(Exception.class,
                () -> userService.scheduleWorkout(userId, workoutId, scheduledAt));

        verify(workoutRepository, never()).findById(workoutId);
        verify(userHasWorkoutsRepository, never()).save(any());
    }

    @Test
    void createScheduledWorkout_workoutNotFound() {
        // Arrange
        Long userId = 1L;
        Long workoutId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(workoutRepository.findById(workoutId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(Exception.class,
                () -> userService.scheduleWorkout(userId, workoutId, ZonedDateTime.now()));

        verify(userHasWorkoutsRepository, never()).save(any());
    }

    @Test
    void createScheduledWorkout_recurringWithoutDayOfWeek() {
        // Arrange
        Long userId = 1L;
        Long workoutId = 1L;
        Set<DayOfWeek> dayOfWeekSet = new HashSet<>();
        LocalTime timeOfDay = LocalTime.now();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(Exception.class,
                () -> userService.scheduleRecurringWorkouts(userId, workoutId, dayOfWeekSet, timeOfDay));

        verify(userRepository).findById(userId);
        verify(workoutRepository, never()).findById(any());
        verify(userHasWorkoutsRepository, never()).save(any());
    }

    @Test
    void updateUser() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername("oldUsername");
        User updatedUser = new User();
        updatedUser.setUsername("newUsername");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        userService.updateUser(userId, updatedUser);

        // Assert
        assertEquals("newUsername", user.getUsername());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateUser_notFound() {
        // Arrange
        Long userId = 1L;
        User updatedUser = new User();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        NoResultException exception = assertThrows(NoResultException.class, () -> userService.updateUser(userId, updatedUser));

        // Assert
        assertEquals("User with ID: 1 could not be found", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteScheduledWorkout() {
        // Arrange
        Long userHasWorkoutsId = 1L;
        UserHasWorkouts userHasWorkouts = new UserHasWorkouts();
        userHasWorkouts.setId(userHasWorkoutsId);

        when(userHasWorkoutsRepository.findById(userHasWorkoutsId)).thenReturn(Optional.of(userHasWorkouts));

        // Act
        userService.deleteScheduledWorkout(userHasWorkoutsId);

        // Assert
        verify(userHasWorkoutsRepository, times(1)).findById(userHasWorkoutsId);
        verify(userHasWorkoutsRepository, times(1)).delete(userHasWorkouts);
    }

    @Test
    void deleteScheduledWorkout_notFound() {
        // Arrange
        Long userHasWorkoutsId = 1L;

        when(userHasWorkoutsRepository.findById(userHasWorkoutsId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoResultException.class, () -> userService.deleteScheduledWorkout(userHasWorkoutsId));

        verify(userHasWorkoutsRepository, times(1)).findById(userHasWorkoutsId);
        verify(userHasWorkoutsRepository, never()).delete(any());
    }
}
