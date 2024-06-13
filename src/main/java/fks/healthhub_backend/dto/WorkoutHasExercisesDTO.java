package fks.healthhub_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkoutHasExercisesDTO {
    private Long id;
    private int sets;
    private int repetitions;
    private double weight;
    private int restTime;
}
