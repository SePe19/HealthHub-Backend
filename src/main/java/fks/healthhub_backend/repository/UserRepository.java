package fks.healthhub_backend.repository;

import fks.healthhub_backend.model.User;
import fks.healthhub_backend.model.Workout;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Qualifier("User")
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    @Query("SELECT w FROM Workout w JOIN w.user u WHERE u.id = :userId")
    List<Workout> findWorkoutsByUserId(Long userId);

    Boolean existsByUsername(String username);
}
