package fks.healthhub_backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@RequiredArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "workouts")
public class Workout {

    @Id
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NonNull
    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    //workout_id and exercise_id are foreign keys to workouts and exercises
    @ElementCollection
    @CollectionTable(name = "workout_has_exercises", joinColumns = @JoinColumn(name = "workout_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"workout_id", "exercise_id"}))
    @AttributeOverrides({
            @AttributeOverride(name = "exerciseName", column = @Column(name = "exercise_name")),
            @AttributeOverride(name = "sets", column = @Column(name = "sets")),
            @AttributeOverride(name = "repetitions", column = @Column(name = "repetitions")),
            @AttributeOverride(name = "weight", column = @Column(name = "weight")),
            @AttributeOverride(name = "restTime", column = @Column(name = "rest_time"))
    })
    private Set<ExerciseDetails> workoutExercises = new HashSet<>();

    public Workout(){}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Workout workout = (Workout) o;
        return Objects.equals(id, workout.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Embeddable
    public static class ExerciseDetails {
        private Long exerciseId;
        private String exerciseName;
        private int sets;
        private int repetitions;
        private Float weight;
        private Integer restTime;
    }
}
