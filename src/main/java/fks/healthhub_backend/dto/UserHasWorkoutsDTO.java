package fks.healthhub_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserHasWorkoutsDTO {
    private Long id;
    private Long userId;
    private String username;
    private Long workoutId;
    private String workoutTitle;
    private String scheduledAt;
    private Boolean completed;
}
