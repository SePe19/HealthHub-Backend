package fks.healthhub_backend.repository;

import fks.healthhub_backend.model.Exercise;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Qualifier("Exercise")
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

}
