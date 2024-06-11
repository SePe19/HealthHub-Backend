package fks.healthhub_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import java.util.Objects;

@RequiredArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "workout_has_exercises")
public class WorkoutHasExercises {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "workout_id", referencedColumnName = "id")
    @JsonIgnoreProperties("workoutHasExercises")
    private Workout workout;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", referencedColumnName = "id")
    @JsonIgnore
    private Exercise exercise;

    @Column(name = "sets")
    private int sets;

    @Column(name = "repetitions")
    private int repetitions;

    @Column(name = "weight")
    private double weight;

    @Column(name = "duration")
    private int duration;

    @Column(name = "rest_time")
    private int restTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkoutHasExercises workoutHasExercises = (WorkoutHasExercises) o;
        return Objects.equals(id, workoutHasExercises.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
