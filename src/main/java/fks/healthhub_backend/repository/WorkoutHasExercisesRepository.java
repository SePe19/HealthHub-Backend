package fks.healthhub_backend.repository;

import fks.healthhub_backend.model.WorkoutHasExercises;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Qualifier("WorkoutHasExercises")
public interface WorkoutHasExercisesRepository extends JpaRepository<WorkoutHasExercises, Long> {
    Optional<WorkoutHasExercises> findByWorkoutIdAndExerciseId(Long workoutId, Long exerciseId);

    void deleteByWorkoutId(Long workoutId);
}