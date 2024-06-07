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
@Table(name = "exercises")
public class Exercise {

    @Id
    @JsonProperty("id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NonNull
    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "exercise_difficulty")
    private ExerciseDifficulty exerciseDifficulty;

    @Column(name = "video_guide")
    private String videoGuide;

    @ElementCollection(targetClass = MuscleGroup.class)
    @CollectionTable(name = "exercise_muscle_groups", joinColumns = @JoinColumn(name = "exercise_id"),
            indexes = {@Index(name = "idx_exercise_id", columnList = "exercise_id"),
                    @Index(name = "idx_muscle_group", columnList = "muscle_group")})
    @Enumerated(EnumType.STRING)
    @Column(name = "muscle_group")
    private Set<MuscleGroup> muscleGroups = new HashSet<>();

    public Exercise(){}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Exercise exercise = (Exercise) o;
        return Objects.equals(id, exercise.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
