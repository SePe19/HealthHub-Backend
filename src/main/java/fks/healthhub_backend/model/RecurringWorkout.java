package fks.healthhub_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@RequiredArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "recurring_workouts")
public class RecurringWorkout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "workout_id", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private Workout workout;

    @Column(name = "time_of_day")
    private LocalTime timeOfDay;

    @ElementCollection(targetClass = DayOfWeek.class)
    @CollectionTable(name = "recurring_workout_days", joinColumns = @JoinColumn(name = "recurring_workout_id"),
            indexes = {@Index(name = "idx_recurring_workout_id", columnList = "recurring_workout_id"),
                    @Index(name = "idx_day_of_week", columnList = "day_of_week")})
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week")
    private Set<DayOfWeek> daysOfWeek = new HashSet<>();

    @NonNull
    @Column(name = "created_at")
    private ZonedDateTime createdAt = ZonedDateTime.now();

    @NonNull
    @Column(name = "updated_at")
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecurringWorkout that = (RecurringWorkout) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

