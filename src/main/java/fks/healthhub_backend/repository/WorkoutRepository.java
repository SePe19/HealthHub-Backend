package fks.healthhub_backend.repository;

import fks.healthhub_backend.model.Workout;
import fks.healthhub_backend.model.WorkoutType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Qualifier("Workout")
public interface WorkoutRepository extends JpaRepository<Workout, Long> {

    List<Workout> findWorkoutsByUserId(Long user_id);

    List<Workout> findWorkoutsByUserIdAndWorkoutType(Long user_id, WorkoutType workoutType);

    @Query("SELECT w FROM Workout w LEFT JOIN FETCH w.workoutHasExercises WHERE w.id = :id")
    Workout findByIdWithExercises(Long id);
}
