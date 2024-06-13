package fks.healthhub_backend.dto;

import fks.healthhub_backend.model.WorkoutType;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class WorkoutDTO {
    private Long id;
    private String title;
    private String description;
    private WorkoutType workoutType;
    private Set<WorkoutHasExercisesDTO> workoutHasExercises;
    private UserDTO user;
}
