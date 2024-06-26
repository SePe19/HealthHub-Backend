package fks.healthhub_backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;

@Getter
@Setter
public class UserHasWorkoutsDTO {
    private Long userId;
    private Long workoutId;
    private boolean recurring;
    private DayOfWeek dayOfWeek;
    private ZonedDateTime scheduledAt;
}
