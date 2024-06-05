package fks.healthhub_backend.repository;

import fks.healthhub_backend.model.Exercise;
import fks.healthhub_backend.model.MuscleGroup;
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

    @Query("SELECT e FROM Exercise e JOIN FETCH e.muscleGroups mg WHERE mg = :muscleGroup")
    List<Exercise> findAllByMuscleGroup(@Param("muscleGroup") MuscleGroup muscleGroup);

    @Query("SELECT e FROM Exercise e WHERE LOWER(e.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Exercise> findByTitleContaining(@Param("title") String title);
}