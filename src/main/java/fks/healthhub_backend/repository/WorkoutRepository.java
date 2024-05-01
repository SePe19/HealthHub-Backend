package fks.healthhub_backend.repository;

import fks.healthhub_backend.model.Workout;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Qualifier("Workout")
public interface WorkoutRepository extends JpaRepository<Workout, Long> {

    List<Workout> findWorkoutsByUserId(Long user_id);

}
