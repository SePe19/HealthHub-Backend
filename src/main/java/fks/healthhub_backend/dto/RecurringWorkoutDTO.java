package fks.healthhub_backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

@Getter
@Setter
public class RecurringWorkoutDTO {
    private Long userId;
    private Long workoutId;
    private Set<DayOfWeek> daysOfWeek;
    private LocalTime timeOfDay;
}
