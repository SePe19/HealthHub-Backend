package fks.healthhub_backend.repository;

import fks.healthhub_backend.model.RecurringWorkout;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Qualifier("RecurringWorkouts")
public interface RecurringWorkoutRepository extends JpaRepository<RecurringWorkout, Long> {
    List<RecurringWorkout> findByUserIdAndWorkoutId(Long userId, Long workoutId);

    List<RecurringWorkout> findByUserId(Long userId);
}
