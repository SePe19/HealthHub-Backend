package fks.healthhub_backend.repository;

import fks.healthhub_backend.model.Exercise;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Qualifier("Exercise")
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    @Query("SELECT e FROM Exercise e WHERE e.id IN :ids")
    List<Exercise> findByIdIn(@Param("ids") List<Long> ids);
}